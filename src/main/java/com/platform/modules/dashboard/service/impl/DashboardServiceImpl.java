package com.platform.modules.dashboard.service.impl;

import com.platform.core.base.BaseServiceImpl;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.modules.dashboard.dto.request.DashboardWidgetRequest;
import com.platform.modules.dashboard.dto.response.DashboardWidgetResponse;
import com.platform.modules.dashboard.entity.DashboardWidget;
import com.platform.modules.dashboard.mapper.DashboardWidgetMapper;
import com.platform.modules.dashboard.repository.DashboardWidgetRepository;
import com.platform.modules.dashboard.service.DashboardService;
import com.platform.modules.user.entity.User;
import com.platform.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardServiceImpl extends BaseServiceImpl<DashboardWidget, DashboardWidgetResponse, DashboardWidgetRepository> implements DashboardService {

    private final DashboardWidgetRepository widgetRepository;
    private final DashboardWidgetMapper widgetMapper;
    private final UserRepository userRepository;

    public DashboardServiceImpl(DashboardWidgetRepository widgetRepository, DashboardWidgetMapper widgetMapper, UserRepository userRepository) {
        super(widgetRepository);
        this.widgetRepository = widgetRepository;
        this.widgetMapper = widgetMapper;
        this.userRepository = userRepository;
    }

    @Override
    public DashboardWidgetResponse create(DashboardWidgetRequest request) {
        DashboardWidget widget = widgetMapper.toEntity(request);
        
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
            widget.setUser(user);
        }
        
        DashboardWidget savedWidget = widgetRepository.save(widget);
        return widgetMapper.toResponse(savedWidget);
    }

    @Override
    public DashboardWidgetResponse create(DashboardWidgetResponse dto) {
        throw new UnsupportedOperationException("Use create(DashboardWidgetRequest) instead");
    }

    @Override
    public DashboardWidgetResponse update(UUID id, DashboardWidgetRequest request) {
        DashboardWidget widget = widgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Widget not found with id: " + id));
        
        widgetMapper.updateEntity(request, widget);
        
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
            widget.setUser(user);
        }
        
        DashboardWidget updatedWidget = widgetRepository.save(widget);
        return widgetMapper.toResponse(updatedWidget);
    }

    @Override
    public DashboardWidgetResponse update(UUID id, DashboardWidgetResponse dto) {
        throw new UnsupportedOperationException("Use update(UUID, DashboardWidgetRequest) instead");
    }

    @Override
    public DashboardWidgetResponse getById(UUID id) {
        DashboardWidget widget = widgetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Widget not found with id: " + id));
        return widgetMapper.toResponse(widget);
    }

    @Override
    public List<DashboardWidgetResponse> getUserWidgets(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return widgetRepository.findByUserAndDeletedFalseOrderByPositionAsc(user).stream()
                .map(widgetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardWidgetResponse> getVisibleUserWidgets(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        return widgetRepository.findByUserAndVisibleTrueAndDeletedFalseOrderByPositionAsc(user).stream()
                .map(widgetMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateWidgetPositions(List<UUID> widgetIds) {
        for (int i = 0; i < widgetIds.size(); i++) {
            UUID id = widgetIds.get(i);
            DashboardWidget widget = widgetRepository.findById(id).orElse(null);
            if (widget != null) {
                widget.setPosition(i);
                widgetRepository.save(widget);
            }
        }
    }

    @Override
    protected DashboardWidgetResponse mapToDto(DashboardWidget entity) {
        return widgetMapper.toResponse(entity);
    }
}
