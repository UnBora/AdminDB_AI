package com.platform.modules.project.service;

import com.platform.core.base.BaseService;
import com.platform.modules.project.dto.request.ProjectRequest;
import com.platform.modules.project.dto.response.ProjectResponse;
import com.platform.modules.project.entity.Project;

import java.util.List;
import java.util.UUID;

public interface ProjectService extends BaseService<Project, ProjectResponse> {
    
    ProjectResponse create(ProjectRequest request);
    
    ProjectResponse update(UUID id, ProjectRequest request);
    
    List<ProjectResponse> getByOwner(UUID ownerId);
    
    List<ProjectResponse> getByStatus(String status);
}
