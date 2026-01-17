package com.issuetracker.controller;

import com.issuetracker.dto.CreateUserRequest;
import com.issuetracker.dto.RoleDTO;
import com.issuetracker.dto.UserDTO;
import com.issuetracker.mapper.RoleMapper;
import com.issuetracker.mapper.UserMapper;
import com.issuetracker.model.User;
import com.issuetracker.service.RoleService;
import com.issuetracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleService roleService;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam(required = false) String query) {
        List<UserDTO> users = userService.searchUsers(query).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(userMapper.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();
        User savedUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDTO(savedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDTO> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDTO> removeRole(@PathVariable Long userId, @PathVariable Long roleId) {
        User user = userService.removeRoleFromUser(userId, roleId);
        return ResponseEntity.ok(userMapper.toDTO(user));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.getAllRoles().stream()
                .map(roleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }
}
