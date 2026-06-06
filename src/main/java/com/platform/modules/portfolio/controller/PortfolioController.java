package com.platform.modules.portfolio.controller;

import com.platform.core.base.ApiResponse;
import com.platform.core.base.PaginationResponse;
import com.platform.modules.portfolio.dto.request.PortfolioRequest;
import com.platform.modules.portfolio.dto.response.PortfolioResponse;
import com.platform.modules.portfolio.service.PortfolioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<PortfolioResponse>>> getAllPortfolios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "desc") String direction) {
        
        PaginationResponse<PortfolioResponse> result = portfolioService.getAllPaginated(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioResponse>> getPortfolioById(@PathVariable UUID id) {
        PortfolioResponse portfolio = portfolioService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(portfolio));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PortfolioResponse>> createPortfolio(@Valid @RequestBody PortfolioRequest request) {
        PortfolioResponse portfolio = portfolioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(portfolio, "Portfolio created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PortfolioResponse>> updatePortfolio(
            @PathVariable UUID id,
            @Valid @RequestBody PortfolioRequest request) {
        PortfolioResponse portfolio = portfolioService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(portfolio, "Portfolio updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePortfolio(@PathVariable UUID id) {
        portfolioService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getPortfoliosByOwner(@PathVariable UUID ownerId) {
        List<PortfolioResponse> portfolios = portfolioService.getByOwner(ownerId);
        return ResponseEntity.ok(ApiResponse.success(portfolios));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getPortfoliosByProject(@PathVariable UUID projectId) {
        List<PortfolioResponse> portfolios = portfolioService.getByProject(projectId);
        return ResponseEntity.ok(ApiResponse.success(portfolios));
    }
}
