package com.platform.core.base;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseController<D, S extends BaseService<? extends BaseEntity, D>> {

    protected final S service;

    @PostMapping
    public ResponseEntity<ApiResponse<D>> create(@RequestBody D dto) {
        D result = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<D>> update(@PathVariable UUID id, @RequestBody D dto) {
        D result = service.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(result, "Updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.ok(ApiResponse.error("Deleted successfully", 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<D>> getById(@PathVariable UUID id) {
        D result = service.getById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PaginationResponse<D>>> getAllPaginated(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        PaginationResponse<D> result = service.getAllPaginated(pageNumber, pageSize, sortBy, sortDirection);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
