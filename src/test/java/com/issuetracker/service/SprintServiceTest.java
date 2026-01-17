package com.issuetracker.service;

import com.issuetracker.exception.BadRequestException;
import com.issuetracker.exception.ResourceNotFoundException;
import com.issuetracker.model.Project;
import com.issuetracker.model.Sprint;
import com.issuetracker.repository.ProjectRepository;
import com.issuetracker.repository.SprintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class SprintServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private SprintService sprintService;

    private Project testProject;
    private Sprint testSprint;

    @BeforeEach
    void setUp() {
        testProject = Project.builder()
                .id(1L)
                .name("Project One")
                .key("P1")
                .build();

        testSprint = Sprint.builder()
                .id(1L)
                .name("Sprint 1")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 14))
                .project(testProject)
                .build();
    }

    @Test
    void createSprint_ShouldReturnSavedSprint() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(sprintRepository.findOverlappingSprints(1L, testSprint.getStartDate(), testSprint.getEndDate()))
                .thenReturn(Arrays.asList());
        when(sprintRepository.save(any(Sprint.class))).thenReturn(testSprint);

        Sprint result = sprintService.createSprint(testSprint);

        assertNotNull(result);
        assertEquals("Sprint 1", result.getName());
        verify(sprintRepository, times(1)).save(any(Sprint.class));
    }

    @Test
    void createSprint_WhenStartDateAfterEndDate_ShouldThrowException() {
        Sprint invalid = Sprint.builder()
                .name("Invalid Sprint")
                .startDate(LocalDate.of(2026, 2, 1))
                .endDate(LocalDate.of(2026, 1, 1))
                .project(testProject)
                .build();

        assertThrows(BadRequestException.class, () -> sprintService.createSprint(invalid));
    }

    @Test
    void createSprint_WhenProjectNotFound_ShouldThrowException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sprintService.createSprint(testSprint));
    }

    @Test
    void createSprint_WhenOverlapping_ShouldThrowException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(sprintRepository.findOverlappingSprints(1L, testSprint.getStartDate(), testSprint.getEndDate()))
                .thenReturn(Arrays.asList(testSprint));

        assertThrows(BadRequestException.class, () -> sprintService.createSprint(testSprint));
    }

    @Test
    void getAllSprints_ShouldReturnSprintList() {
        when(sprintRepository.findAll()).thenReturn(Arrays.asList(testSprint));

        List<Sprint> result = sprintService.getAllSprints();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSprintById_WhenExists_ShouldReturnSprint() {
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(testSprint));

        Optional<Sprint> result = sprintService.getSprintById(1L);

        assertTrue(result.isPresent());
        assertEquals("Sprint 1", result.get().getName());
    }

    @Test
    void updateSprint_ShouldReturnUpdatedSprint() {
        Sprint updated = Sprint.builder()
                .id(1L)
                .name("Sprint 1 Updated")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 14))
                .project(testProject)
                .build();

        when(sprintRepository.findById(1L)).thenReturn(Optional.of(testSprint));
        when(sprintRepository.findOverlappingSprints(1L, updated.getStartDate(), updated.getEndDate()))
                .thenReturn(Arrays.asList(testSprint));
        when(sprintRepository.save(any(Sprint.class))).thenReturn(updated);

        Sprint result = sprintService.updateSprint(updated);

        assertNotNull(result);
        assertEquals("Sprint 1 Updated", result.getName());
    }

    @Test
    void updateSprint_WhenSprintNotFound_ShouldThrowException() {
        when(sprintRepository.findById(99L)).thenReturn(Optional.empty());

        Sprint updated = Sprint.builder()
                .id(99L)
                .name("Sprint 99")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 14))
                .project(testProject)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> sprintService.updateSprint(updated));
    }

    @Test
    void updateSprint_WhenStartDateAfterEndDate_ShouldThrowException() {
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(testSprint));

        Sprint invalid = Sprint.builder()
                .id(1L)
                .name("Invalid Sprint")
                .startDate(LocalDate.of(2026, 2, 1))
                .endDate(LocalDate.of(2026, 1, 1))
                .project(testProject)
                .build();

        assertThrows(BadRequestException.class, () -> sprintService.updateSprint(invalid));
    }

    @Test
    void updateSprint_WhenProjectChangedAndNotFound_ShouldThrowException() {
        Project otherProject = Project.builder().id(2L).build();
        Sprint updated = Sprint.builder()
                .id(1L)
                .name("Sprint 1")
                .startDate(LocalDate.of(2026, 1, 1))
                .endDate(LocalDate.of(2026, 1, 14))
                .project(otherProject)
                .build();

        when(sprintRepository.findById(1L)).thenReturn(Optional.of(testSprint));
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sprintService.updateSprint(updated));
    }

    @Test
    void updateSprint_WhenOverlappingOtherSprint_ShouldThrowException() {
        Sprint otherSprint = Sprint.builder()
                .id(2L)
                .name("Sprint 2")
                .startDate(LocalDate.of(2026, 1, 10))
                .endDate(LocalDate.of(2026, 1, 20))
                .project(testProject)
                .build();

        when(sprintRepository.findById(1L)).thenReturn(Optional.of(testSprint));
        when(sprintRepository.findOverlappingSprints(1L, testSprint.getStartDate(), testSprint.getEndDate()))
                .thenReturn(Arrays.asList(otherSprint));

        assertThrows(BadRequestException.class, () -> sprintService.updateSprint(testSprint));
    }

    @Test
    void deleteSprint_ShouldDeleteSprint() {
        when(sprintRepository.findById(1L)).thenReturn(Optional.of(testSprint));
        doNothing().when(sprintRepository).delete(any(Sprint.class));

        sprintService.deleteSprint(1L);

        verify(sprintRepository, times(1)).delete(any(Sprint.class));
    }

    @Test
    void deleteSprint_WhenSprintNotFound_ShouldThrowException() {
        when(sprintRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sprintService.deleteSprint(99L));
    }

    @Test
    void getSprintsByProject_ShouldReturnSprintList() {
        when(sprintRepository.findByProjectId(1L)).thenReturn(Arrays.asList(testSprint));

        List<Sprint> result = sprintService.getSprintsByProject(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getActiveSprintsByProject_ShouldReturnActiveSprints() {
        when(sprintRepository.findActiveSprintsByProjectAndDate(eq(1L), any(LocalDate.class)))
                .thenReturn(Arrays.asList(testSprint));

        List<Sprint> result = sprintService.getActiveSprintsByProject(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSprintsByProjectOrderByStartDate_ShouldReturnSprints() {
        when(sprintRepository.findByProjectIdOrderByStartDateDesc(1L)).thenReturn(Arrays.asList(testSprint));

        List<Sprint> result = sprintService.getSprintsByProjectOrderByStartDate(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
