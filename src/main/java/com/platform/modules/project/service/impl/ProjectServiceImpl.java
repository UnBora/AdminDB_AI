package com.platform.modules.project.service.impl;

import com.platform.core.base.BaseServiceImpl;
import com.platform.core.exception.ResourceNotFoundException;
import com.platform.modules.project.dto.request.ProjectRequest;
import com.platform.modules.project.dto.response.ProjectResponse;
import com.platform.modules.project.entity.Project;
import com.platform.modules.project.mapper.ProjectMapper;
import com.platform.modules.project.repository.ProjectRepository;
import com.platform.modules.project.service.ProjectService;
import com.platform.modules.user.entity.User;
import com.platform.modules.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl extends BaseServiceImpl<Project, ProjectResponse, ProjectRepository> implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, ProjectMapper projectMapper, UserRepository userRepository) {
        super(projectRepository);
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.userRepository = userRepository;
    }

    @Override
    public ProjectResponse create(ProjectRequest request) {
        Project project = projectMapper.toEntity(request);
        
        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            project.setOwner(owner);
        }
        
        Project savedProject = projectRepository.save(project);
        return projectMapper.toResponse(savedProject);
    }

    @Override
    public ProjectResponse create(ProjectResponse dto) {
        throw new UnsupportedOperationException("Use create(ProjectRequest) instead");
    }

    @Override
    public ProjectResponse update(UUID id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        
        projectMapper.updateEntity(request, project);
        
        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            project.setOwner(owner);
        }
        
        Project updatedProject = projectRepository.save(project);
        return projectMapper.toResponse(updatedProject);
    }

    @Override
    public ProjectResponse update(UUID id, ProjectResponse dto) {
        throw new UnsupportedOperationException("Use update(UUID, ProjectRequest) instead");
    }

    @Override
    public ProjectResponse getById(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectResponse> getByOwner(UUID ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + ownerId));
        
        return projectRepository.findByOwnerAndDeletedFalse(owner).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectResponse> getByStatus(String status) {
        return projectRepository.findByStatusAndDeletedFalse(status).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    protected ProjectResponse mapToDto(Project entity) {
        return projectMapper.toResponse(entity);
    }
}
