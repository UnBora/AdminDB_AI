package com.platform.modules.settings.controller;

import com.platform.core.base.ApiResponse;
import com.platform.modules.settings.dto.request.SettingUpdateRequest;
import com.platform.modules.settings.dto.response.SettingListResponse;
import com.platform.modules.settings.dto.response.SettingResponse;
import com.platform.modules.settings.dto.response.SmtpTestResponse;
import com.platform.modules.settings.entity.SettingCategory;
import com.platform.modules.settings.service.SettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
@Slf4j
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SettingListResponse>> getAllSettings() {
        log.info("Fetching all settings");
        List<SettingResponse> settings = settingService.getAllSettings();
        
        SettingListResponse response = SettingListResponse.builder()
                .settings(settings)
                .totalCount(settings.size())
                .pageNumber(1)
                .pageSize(settings.size())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Settings retrieved successfully"));
    }

    @GetMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SettingResponse>> getSetting(@PathVariable String key) {
        log.info("Fetching setting: {}", key);
        SettingResponse setting = settingService.getSetting(key);
        return ResponseEntity.ok(ApiResponse.success(setting, "Setting retrieved successfully"));
    }

    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SettingListResponse>> getSettingsByCategory(@PathVariable String category) {
        log.info("Fetching settings for category: {}", category);
        
        SettingCategory settingCategory = SettingCategory.fromString(category);
        List<SettingResponse> settings = settingService.getSettingsByCategory(settingCategory);
        
        SettingListResponse response = SettingListResponse.builder()
                .settings(settings)
                .totalCount(settings.size())
                .pageNumber(1)
                .pageSize(settings.size())
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Category settings retrieved successfully"));
    }

    @PutMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SettingResponse>> updateSetting(
            @PathVariable String key,
            @RequestBody SettingUpdateRequest request) {
        log.info("Updating setting: {} with value: {}", key, request.getValue());
        
        SettingResponse setting = settingService.setSetting(
                key,
                request.getValue(),
                request.getCategory(),
                request.getDescription()
        );
        
        return ResponseEntity.ok(ApiResponse.success(setting, "Setting updated successfully"));
    }

    @DeleteMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSetting(@PathVariable String key) {
        log.info("Deleting setting: {}", key);
        settingService.deleteSetting(key);
        return ResponseEntity.ok(ApiResponse.success(null, "Setting deleted successfully"));
    }

    @PostMapping("/cache-refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> refreshCache() {
        log.info("Refreshing settings cache");
        settingService.refreshCache();
        return ResponseEntity.ok(ApiResponse.success(null, "Settings cache refreshed successfully"));
    }

    @PostMapping("/test-smtp")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SmtpTestResponse>> testSmtpConnection() {
        log.info("Testing SMTP connection");
        
        boolean success = settingService.testSmtpConnection();
        SmtpTestResponse response = SmtpTestResponse.builder()
                .success(success)
                .message(success ? "SMTP connection successful" : "SMTP connection failed")
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(response, "SMTP test completed"));
    }
}
