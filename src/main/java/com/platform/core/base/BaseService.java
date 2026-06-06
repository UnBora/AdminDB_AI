package com.platform.core.base;

import java.util.UUID;

public interface BaseService<T extends BaseEntity, D> {

    D create(D dto);

    D update(UUID id, D dto);

    void delete(UUID id);

    void softDelete(UUID id);

    D getById(UUID id);

    PaginationResponse<D> getAllPaginated(int pageNumber, int pageSize, String sortBy, String sortDirection);
}
