package com.issuetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.issuetracker.dto.CreateUserRequest;
import com.issuetracker.mapper.RoleMapper;
import com.issuetracker.mapper.UserMapper;
import com.issuetracker.model.Role;
import com.issuetracker.model.User;
import com.issuetracker.dto.RoleDTO;
import com.issuetracker.dto.UserDTO;
import com.issuetracker.service.RoleService;
import com.issuetracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings({"null", "removal"})
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private RoleMapper roleMapper;

    private User testUser;
    private Role developerRole;
    private UserDTO testUserDTO;
    private RoleDTO developerRoleDTO;

    @BeforeEach
    void setUp() {
        developerRole = Role.builder()
                .id(1L)
                .name("DEVELOPER")
                .build();

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .roles(new HashSet<>())
                .build();

        testUserDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .roles(new HashSet<>())
                .build();

        developerRoleDTO = RoleDTO.builder()
                .id(1L)
                .name("DEVELOPER")
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnUserList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getAllUsers_WhenNoUsers_ShouldReturnEmptyList() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchUsers_ShouldReturnUserList() throws Exception {
        when(userService.searchUsers("test")).thenReturn(Arrays.asList(testUser));
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users/search").param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void searchUsers_WithBlankQuery_ShouldReturnAllUsers() throws Exception {
        when(userService.searchUsers("")).thenReturn(Arrays.asList(testUser));
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users/search").param("query", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getUserById_WhenNotExists_ShouldReturn404() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();

        User savedUser = User.builder()
                .id(2L)
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .active(true)
                .roles(new HashSet<>())
                .build();

        when(userService.createUser(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(UserDTO.builder()
                .id(2L)
                .username("newuser")
                .email("new@example.com")
                .build());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void assignRoleToUser_ShouldReturnUpdatedUser() throws Exception {
        Set<Role> roles = new HashSet<>();
        roles.add(developerRole);
        testUser.setRoles(roles);

        when(userService.assignRoleToUser(eq(1L), eq(1L))).thenReturn(testUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(UserDTO.builder()
                .id(1L)
                .username("testuser")
                .roles(Set.of("DEVELOPER"))
                .build());

        mockMvc.perform(post("/api/users/1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles").isArray())
                .andExpect(jsonPath("$.roles[0]").value("DEVELOPER"));
    }

    @Test
    void removeRoleFromUser_ShouldReturnUpdatedUser() throws Exception {
        when(userService.removeRoleFromUser(eq(1L), eq(1L))).thenReturn(testUser);
        when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

        mockMvc.perform(delete("/api/users/1/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void getAllRoles_ShouldReturnRoleList() throws Exception {
        Role adminRole = Role.builder().id(2L).name("ADMIN").build();
        when(roleService.getAllRoles()).thenReturn(Arrays.asList(developerRole, adminRole));
        when(roleMapper.toDTO(developerRole)).thenReturn(developerRoleDTO);
        when(roleMapper.toDTO(adminRole)).thenReturn(RoleDTO.builder().id(2L).name("ADMIN").build());

        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("DEVELOPER"))
                .andExpect(jsonPath("$[1].name").value("ADMIN"));
    }

    @Test
    void getAllRoles_WhenNoRoles_ShouldReturnEmptyList() throws Exception {
        when(roleService.getAllRoles()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/users/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
