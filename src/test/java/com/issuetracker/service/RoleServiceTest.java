package com.issuetracker.service;

import com.issuetracker.model.Role;
import com.issuetracker.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role adminRole;
    private Role developerRole;

    @BeforeEach
    void setUp() {
        adminRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        developerRole = Role.builder()
                .id(2L)
                .name("DEVELOPER")
                .build();
    }

    @Test
    void getAllRoles_ShouldReturnAllRoles() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList(adminRole, developerRole));

        List<Role> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void getAllRoles_WhenNoRoles_ShouldReturnEmptyList() {
        when(roleRepository.findAll()).thenReturn(Arrays.asList());

        List<Role> result = roleService.getAllRoles();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRoleById_WhenExists_ShouldReturnRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(adminRole));

        Optional<Role> result = roleService.getRoleById(1L);

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
    }

    @Test
    void getRoleById_WhenNotExists_ShouldReturnEmpty() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void getRoleByName_WhenExists_ShouldReturnRole() {
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));

        Optional<Role> result = roleService.getRoleByName("ADMIN");

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getRoleByName_WhenNotExists_ShouldReturnEmpty() {
        when(roleRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        Optional<Role> result = roleService.getRoleByName("UNKNOWN");

        assertFalse(result.isPresent());
    }
}
