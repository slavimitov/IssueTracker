package com.issuetracker.service;

import com.issuetracker.model.Role;
import com.issuetracker.model.User;
import com.issuetracker.repository.RoleRepository;
import com.issuetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
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

        testRole = Role.builder()
                .id(1L)
                .name("DEVELOPER")
                .users(new HashSet<>())
                .build();
    }

    @Test
    void createUser_ShouldReturnSavedUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturnEmpty() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void getUserByUsername_WhenExists_ShouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void getUserByUsername_WhenNotExists_ShouldReturnEmpty() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByUsername("unknown");

        assertFalse(result.isPresent());
    }

    @Test
    void getUserByEmail_WhenExists_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.getUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void getUserByEmail_WhenNotExists_ShouldReturnEmpty() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByEmail("unknown@example.com");

        assertFalse(result.isPresent());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        testUser.setFirstName("Updated");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(testUser);

        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldCallRepositoryDelete() {
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void assignRoleToUser_ShouldAddRoleToUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.assignRoleToUser(1L, 1L);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void assignRoleToUser_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.assignRoleToUser(99L, 1L);
        });
    }

    @Test
    void assignRoleToUser_WhenRoleNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.assignRoleToUser(1L, 99L);
        });
    }

    @Test
    void removeRoleFromUser_ShouldRemoveRoleFromUser() {
        testUser.getRoles().add(testRole);
        testRole.getUsers().add(testUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.removeRoleFromUser(1L, 1L);

        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void removeRoleFromUser_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.removeRoleFromUser(99L, 1L);
        });
    }

    @Test
    void existsByUsername_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        boolean result = userService.existsByUsername("testuser");

        assertTrue(result);
    }

    @Test
    void existsByUsername_WhenNotExists_ShouldReturnFalse() {
        when(userRepository.existsByUsername("unknown")).thenReturn(false);

        boolean result = userService.existsByUsername("unknown");

        assertFalse(result);
    }

    @Test
    void existsByEmail_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_WhenNotExists_ShouldReturnFalse() {
        when(userRepository.existsByEmail("unknown@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("unknown@example.com");

        assertFalse(result);
    }

    @Test
    void searchUsers_WithQuery_ShouldReturnMatches() {
        when(userRepository.searchByUsernameOrEmail("test")).thenReturn(Arrays.asList(testUser));

        List<User> result = userService.searchUsers("test");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchUsers_WithBlankQuery_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        List<User> result = userService.searchUsers(" ");

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
