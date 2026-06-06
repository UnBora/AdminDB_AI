package com.platform.modules.portfolio.service.impl;

import com.platform.core.base.BaseServiceImpl;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.modules.portfolio.dto.request.PortfolioRequest;
import com.platform.modules.portfolio.dto.response.PortfolioResponse;
import com.platform.modules.portfolio.entity.Portfolio;
import com.platform.modules.portfolio.mapper.PortfolioMapper;
import com.platform.modules.portfolio.repository.PortfolioRepository;
import com.platform.modules.portfolio.service.PortfolioService;
import com.platform.modules.project.entity.Project;
import com.platform.modules.project.repository.ProjectRepository;
import com.platform.modules.user.entity.User;
import com.platform.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PortfolioServiceImpl extends BaseServiceImpl<Portfolio, PortfolioResponse, PortfolioRepository> implements PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository, PortfolioMapper portfolioMapper, 
                               ProjectRepository projectRepository, UserRepository userRepository) {
        super(portfolioRepository);
        this.portfolioRepository = portfolioRepository;
        this.portfolioMapper = portfolioMapper;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PortfolioResponse create(PortfolioRequest request) {
        Portfolio portfolio = portfolioMapper.toEntity(request);
        
        resolveDependencies(request, portfolio);
        
        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toResponse(savedPortfolio);
    }

    @Override
    public PortfolioResponse create(PortfolioResponse dto) {
        throw new UnsupportedOperationException("Use create(PortfolioRequest) instead");
    }

    @Override
    public PortfolioResponse update(UUID id, PortfolioRequest request) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        
        portfolioMapper.updateEntity(request, portfolio);
        resolveDependencies(request, portfolio);
        
        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toResponse(updatedPortfolio);
    }

    @Override
    public PortfolioResponse update(UUID id, PortfolioResponse dto) {
        throw new UnsupportedOperationException("Use update(UUID, PortfolioRequest) instead");
    }

    @Override
    public PortfolioResponse getById(UUID id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + id));
        return portfolioMapper.toResponse(portfolio);
    }

    @Override
    public List<PortfolioResponse> getByOwner(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
        
        return portfolioRepository.findByOwnerAndDeletedFalse(owner).stream()
                .map(portfolioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PortfolioResponse> getByProject(UUID projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        
        return portfolioRepository.findByProjectAndDeletedFalse(project).stream()
                .map(portfolioMapper::toResponse)
                .collect(Collectors.toList());
    }

    private void resolveDependencies(PortfolioRequest request, Portfolio portfolio) {
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
            portfolio.setProject(project);
        }
        
        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            portfolio.setOwner(owner);
        }
    }

    @Override
    protected PortfolioResponse mapToDto(Portfolio entity) {
        return portfolioMapper.toResponse(entity);
    }
}
