package com.platform.modules.portfolio.service;

import com.platform.core.base.BaseService;
import com.platform.modules.portfolio.dto.request.PortfolioRequest;
import com.platform.modules.portfolio.dto.response.PortfolioResponse;
import com.platform.modules.portfolio.entity.Portfolio;

import java.util.List;
import java.util.UUID;

public interface PortfolioService extends BaseService<Portfolio, PortfolioResponse> {
    
    PortfolioResponse create(PortfolioRequest request);
    
    PortfolioResponse update(UUID id, PortfolioRequest request);
    
    List<PortfolioResponse> getByOwner(UUID ownerId);
    
    List<PortfolioResponse> getByProject(UUID projectId);
}
