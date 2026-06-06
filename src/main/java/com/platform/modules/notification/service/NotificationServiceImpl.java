package com.platform.modules.notification.service;

import com.platform.core.base.BaseServiceImpl;
import com.platform.core.base.PaginationResponse;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.modules.notification.dto.request.NotificationCreateRequest;
import com.platform.modules.notification.dto.response.NotificationListResponse;
import com.platform.modules.notification.dto.response.NotificationPreferenceResponse;
import com.platform.modules.notification.dto.response.NotificationResponse;
import com.platform.modules.notification.entity.Notification;
import com.platform.modules.notification.entity.NotificationPreference;
import com.platform.modules.notification.entity.NotificationType;
import com.platform.modules.notification.repository.NotificationPreferenceRepository;
import com.platform.modules.notification.repository.NotificationRepository;
import com.platform.modules.user.entity.User;
import com.platform.modules.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class NotificationServiceImpl extends BaseServiceImpl<Notification, NotificationResponse, NotificationRepository> 
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final Map<UUID, Long> unreadCountCache = new ConcurrentHashMap<>();

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                NotificationPreferenceRepository preferenceRepository,
                                UserRepository userRepository) {
        super(notificationRepository);
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .title(request.getTitle())
                .message(request.getMessage())
                .type(request.getType())
                .actionUrl(request.getActionUrl())
                .data(request.getData())
                .priority(request.getPriority())
                .expiresAt(request.getExpiresAt())
                .read(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        invalidateUnreadCountCache(user.getId());
        log.info("Notification created for user: {}", user.getId());

        return mapToResponse(saved);
    }

    @Override
    public NotificationListResponse getNotifications(UUID userId, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Notification> notificationPage = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        List<NotificationResponse> responses = notificationPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        long unreadCount = getUnreadCount(userId);

        return NotificationListResponse.builder()
                .notifications(responses)
                .unreadCount(unreadCount)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .build();
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "unreadCount", key = "#notificationId")
    public NotificationResponse markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.markAsRead();
        Notification saved = notificationRepository.save(notification);
        invalidateUnreadCountCache(notification.getUser().getId());

        return mapToResponse(saved);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
        unreadNotifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(unreadNotifications);
        invalidateUnreadCountCache(userId);
        log.info("Marked all notifications as read for user: {}", userId);
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notificationRepository.delete(notification);
        invalidateUnreadCountCache(notification.getUser().getId());
        log.info("Notification deleted: {}", notificationId);
    }

    @Override
    @Cacheable(value = "unreadCount", key = "#userId")
    public long getUnreadCount(UUID userId) {
        return unreadCountCache.computeIfAbsent(userId, id -> notificationRepository.countUnreadByUserId(id));
    }

    @Override
    public void sendSystemNotification(List<UUID> recipientIds, String title, String message) {
        for (UUID userId : recipientIds) {
            sendSystemNotificationToUser(userId, title, message);
        }
        log.info("System notification sent to {} recipients", recipientIds.size());
    }

    @Override
    public void sendSystemNotificationToUser(UUID userId, String title, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(NotificationType.SYSTEM_ALERT)
                .priority(com.platform.modules.notification.entity.NotificationPriority.HIGH)
                .read(false)
                .build();

        notificationRepository.save(notification);
        invalidateUnreadCountCache(userId);
    }

    @Override
    public List<NotificationPreferenceResponse> getUserPreferences(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return preferenceRepository.findByUserId(userId).stream()
                .map(this::mapPreferenceToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationPreferenceResponse updatePreference(UUID userId, String notificationType, NotificationPreferenceResponse request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        NotificationType type = NotificationType.valueOf(notificationType.toUpperCase());
        NotificationPreference preference = preferenceRepository.findByUserIdAndType(userId, type)
                .orElseGet(() -> NotificationPreference.builder()
                        .user(user)
                        .notificationType(type)
                        .build());

        if (request.getEmailEnabled() != null) {
            preference.setEmailEnabled(request.getEmailEnabled());
        }
        if (request.getPushEnabled() != null) {
            preference.setPushEnabled(request.getPushEnabled());
        }
        if (request.getInAppEnabled() != null) {
            preference.setInAppEnabled(request.getInAppEnabled());
        }
        if (request.getEnabled() != null) {
            preference.setEnabled(request.getEnabled());
        }

        NotificationPreference saved = preferenceRepository.save(preference);
        return mapPreferenceToResponse(saved);
    }

    @Override
    @Transactional
    public void cleanupExpiredNotifications() {
        notificationRepository.deleteExpiredNotifications();
        log.info("Expired notifications cleaned up");
    }

    @Override
    public NotificationResponse create(NotificationResponse dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .title(dto.getTitle())
                .message(dto.getMessage())
                .type(dto.getType())
                .priority(dto.getPriority())
                .user(user)
                .read(false)
                .actionUrl(dto.getActionUrl())
                .data(dto.getData())
                .expiresAt(dto.getExpiresAt())
                .build();

        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    public NotificationResponse update(UUID id, NotificationResponse dto) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setTitle(dto.getTitle());
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());
        notification.setPriority(dto.getPriority());
        return mapToResponse(notificationRepository.save(notification));
    }

    @Override
    public NotificationResponse getById(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        return mapToResponse(notification);
    }

    @Override
    public PaginationResponse<NotificationResponse> getAllPaginated(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, Sort.by(direction, sortBy));
        Page<Notification> page = notificationRepository.findAll(pageable);
        
        List<NotificationResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PaginationResponse.<NotificationResponse>builder()
                .content(content)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isLast(page.isLast())
                .build();
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getCreatedBy())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.getRead())
                .readAt(notification.getReadAt())
                .actionUrl(notification.getActionUrl())
                .data(notification.getData())
                .priority(notification.getPriority())
                .expiresAt(notification.getExpiresAt())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    private NotificationPreferenceResponse mapPreferenceToResponse(NotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
                .notificationType(preference.getNotificationType().name())
                .emailEnabled(preference.getEmailEnabled())
                .pushEnabled(preference.getPushEnabled())
                .inAppEnabled(preference.getInAppEnabled())
                .enabled(preference.getEnabled())
                .build();
    }

    private void invalidateUnreadCountCache(UUID userId) {
        unreadCountCache.remove(userId);
    }

    @Override
    protected NotificationResponse mapToDto(Notification entity) {
        return mapToResponse(entity);
    }
}
