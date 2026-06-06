package com.platform.modules.project.service.impl;

import com.platform.core.exception.ResourceNotFoundException;
import com.platform.modules.project.dto.request.ProjectRequest;
import com.platform.modules.project.dto.response.ProjectResponse;
import com.platform.modules.project.entity.Project;
import com.platform.modules.project.mapper.ProjectMapper;
import com.platform.modules.project.repository.ProjectRepository;
import com.platform.modules.user.entity.User;
import com.platform.modules.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private ProjectRequest projectRequest;
    private Project project;
    private ProjectResponse projectResponse;
    private UUID projectId;
    private UUID ownerId;
    private User owner;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        
        projectRequest = ProjectRequest.builder()
                .name("Test Project")
                .description("Test Description")
                .status("ACTIVE")
                .ownerId(ownerId)
                .build();

        owner = new User();
        owner.setId(ownerId);
        owner.setUsername("testuser");

        project = new Project();
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setStatus("ACTIVE");
        project.setOwner(owner);
        project.setId(projectId);

        projectResponse = ProjectResponse.builder()
                .id(projectId)
                .name("Test Project")
                .status("ACTIVE")
                .build();
    }

    @Test
    void create_ShouldReturnProjectResponse_WhenSuccessful() {
        // Arrange
        when(projectMapper.toEntity(any(ProjectRequest.class))).thenReturn(project);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(projectRepository.save(any(Project.class))).thenReturn(project);
        when(projectMapper.toResponse(any(Project.class))).thenReturn(projectResponse);

        // Act
        ProjectResponse result = projectService.create(projectRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(projectId);
        verify(projectRepository).save(any(Project.class));
        verify(userRepository).findById(ownerId);
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenOwnerNotFound() {
        // Arrange
        when(projectMapper.toEntity(any(ProjectRequest.class))).thenReturn(project);
        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.create(projectRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Owner not found");
        
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getById_ShouldReturnProjectResponse_WhenProjectExists() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMapper.toResponse(project)).thenReturn(projectResponse);

        // Act
        ProjectResponse result = projectService.getById(projectId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(projectId);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenProjectDoesNotExist() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.getById(projectId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    void softDelete_ShouldSetDeletedToTrue() {
        // Arrange
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Act
        projectService.softDelete(projectId);

        // Assert
        assertThat(project.getDeleted()).isTrue();
        verify(projectRepository).save(project);
    }
}
