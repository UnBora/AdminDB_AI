package com.platform.modules.dashboard.controller;

import com.platform.core.base.ApiResponse;
import com.platform.core.base.PaginationResponse;
import com.platform.modules.dashboard.dto.request.DashboardWidgetRequest;
import com.platform.modules.dashboard.dto.response.DashboardWidgetResponse;
import com.platform.modules.dashboard.service.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard/widgets")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class DashboardWidgetController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<DashboardWidgetResponse>>> getAllWidgets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "position") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        PaginationResponse<DashboardWidgetResponse> result = dashboardService.getAllPaginated(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DashboardWidgetResponse>> getWidgetById(@PathVariable UUID id) {
        DashboardWidgetResponse widget = dashboardService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(widget));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DashboardWidgetResponse>> createWidget(@Valid @RequestBody DashboardWidgetRequest request) {
        DashboardWidgetResponse widget = dashboardService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(widget, "Widget created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DashboardWidgetResponse>> updateWidget(
            @PathVariable UUID id,
            @Valid @RequestBody DashboardWidgetRequest request) {
        DashboardWidgetResponse widget = dashboardService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(widget, "Widget updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWidget(@PathVariable UUID id) {
        dashboardService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<DashboardWidgetResponse>>> getUserWidgets(@PathVariable UUID userId) {
        List<DashboardWidgetResponse> widgets = dashboardService.getUserWidgets(userId);
        return ResponseEntity.ok(ApiResponse.success(widgets));
    }

    @GetMapping("/user/{userId}/visible")
    public ResponseEntity<ApiResponse<List<DashboardWidgetResponse>>> getVisibleUserWidgets(@PathVariable UUID userId) {
        List<DashboardWidgetResponse> widgets = dashboardService.getVisibleUserWidgets(userId);
        return ResponseEntity.ok(ApiResponse.success(widgets));
    }

    @PostMapping("/reorder")
    public ResponseEntity<ApiResponse<Void>> reorderWidgets(@RequestBody List<UUID> widgetIds) {
        dashboardService.updateWidgetPositions(widgetIds);
        return ResponseEntity.ok(ApiResponse.success(null, "Widgets reordered successfully"));
    }
}
