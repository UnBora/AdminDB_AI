package com.platform.core.base;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class BaseServiceImpl<T extends BaseEntity, D, R extends BaseRepository<T>> implements BaseService<T, D> {

    protected final R repository;

    @Override
    public abstract D create(D dto);

    @Override
    public abstract D update(UUID id, D dto);

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void softDelete(UUID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entity not found with id: " + id));
        entity.setDeleted(true);
        repository.save(entity);
    }

    @Override
    public abstract D getById(UUID id);

    @Override
    public PaginationResponse<D> getAllPaginated(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));

        Page<T> page = repository.findAll(pageable);

        return PaginationResponse.<D>builder()
                .content(page.getContent().stream().map(this::mapToDto).toList())
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    protected abstract D mapToDto(T entity);
}
