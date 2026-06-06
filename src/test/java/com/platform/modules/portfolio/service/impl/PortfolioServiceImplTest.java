package com.platform.modules.portfolio.service.impl;

import com.platform.core.exception.ResourceNotFoundException;
import com.platform.modules.portfolio.dto.request.PortfolioRequest;
import com.platform.modules.portfolio.dto.response.PortfolioResponse;
import com.platform.modules.portfolio.entity.Portfolio;
import com.platform.modules.portfolio.mapper.PortfolioMapper;
import com.platform.modules.portfolio.repository.PortfolioRepository;
import com.platform.modules.project.entity.Project;
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
class PortfolioServiceImplTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private PortfolioMapper portfolioMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PortfolioServiceImpl portfolioService;

    private PortfolioRequest portfolioRequest;
    private Portfolio portfolio;
    private PortfolioResponse portfolioResponse;
    private UUID portfolioId;
    private UUID projectId;
    private UUID ownerId;
    private Project project;
    private User owner;

    @BeforeEach
    void setUp() {
        portfolioId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        portfolioRequest = PortfolioRequest.builder()
                .title("Test Portfolio")
                .description("Test Description")
                .projectId(projectId)
                .ownerId(ownerId)
                .build();

        project = new Project();
        project.setId(projectId);
        project.setName("Test Project");

        owner = new User();
        owner.setId(ownerId);
        owner.setUsername("testuser");

        portfolio = new Portfolio();
        portfolio.setTitle("Test Portfolio");
        portfolio.setDescription("Test Description");
        portfolio.setProject(project);
        portfolio.setOwner(owner);
        portfolio.setId(portfolioId);

        portfolioResponse = PortfolioResponse.builder()
                .id(portfolioId)
                .title("Test Portfolio")
                .build();
    }

    @Test
    void create_ShouldReturnPortfolioResponse_WhenSuccessful() {
        // Arrange
        when(portfolioMapper.toEntity(any(PortfolioRequest.class))).thenReturn(portfolio);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(portfolio);
        when(portfolioMapper.toResponse(any(Portfolio.class))).thenReturn(portfolioResponse);

        // Act
        PortfolioResponse result = portfolioService.create(portfolioRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(portfolioId);
        verify(portfolioRepository).save(any(Portfolio.class));
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(ownerId);
    }

    @Test
    void create_ShouldThrowResourceNotFoundException_WhenProjectNotFound() {
        // Arrange
        when(portfolioMapper.toEntity(any(PortfolioRequest.class))).thenReturn(portfolio);
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> portfolioService.create(portfolioRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found");
        
        verify(portfolioRepository, never()).save(any(Portfolio.class));
    }

    @Test
    void getById_ShouldReturnPortfolioResponse_WhenPortfolioExists() {
        // Arrange
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));
        when(portfolioMapper.toResponse(portfolio)).thenReturn(portfolioResponse);

        // Act
        PortfolioResponse result = portfolioService.getById(portfolioId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(portfolioId);
    }

    @Test
    void softDelete_ShouldSetDeletedToTrue() {
        // Arrange
        when(portfolioRepository.findById(portfolioId)).thenReturn(Optional.of(portfolio));

        // Act
        portfolioService.softDelete(portfolioId);

        // Assert
        assertThat(portfolio.getDeleted()).isTrue();
        verify(portfolioRepository).save(portfolio);
    }
}
