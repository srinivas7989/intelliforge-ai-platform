package com.aiproject.auth_service.controller;

import com.aiproject.auth_service.dto.ChangePasswordRequest;
import com.aiproject.auth_service.dto.UpdateProfileRequest;
import com.aiproject.auth_service.entity.Role;
import com.aiproject.auth_service.entity.User;
import com.aiproject.auth_service.exception.BadRequestException;
import com.aiproject.auth_service.exception.ResourceNotFoundException;
import com.aiproject.auth_service.exception.UnauthorizedException;
import com.aiproject.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final Authentication authentication = mock(Authentication.class);

    private final UserController userController =
            new UserController(userRepository, passwordEncoder);

    @Test
    void me_ShouldReturnCurrentUser() {

        Role role = Role.builder()
                .id(1L)
                .name("USER")
                .build();

        User user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("encoded")
                .roles(Set.of(role))
                .enabled(true)
                .accountLocked(false)
                .build();

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        var response = userController.me(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void me_ShouldThrowUnauthorized_WhenAuthenticationIsNull() {

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> userController.me(null)
        );

        assertEquals(
                "Unauthorized: Missing or invalid token",
                exception.getMessage()
        );
    }

    @Test
    void me_ShouldThrowResourceNotFound_WhenUserNotFound() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("missing@example.com");

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userController.me(authentication)
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );
    }

    @Test
    void updateProfile_ShouldUpdateUserName() {

        User user = User.builder()
                .id(1L)
                .name("Old Name")
                .email("john@example.com")
                .password("encoded")
                .roles(Set.of())
                .enabled(true)
                .accountLocked(false)
                .build();

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        UpdateProfileRequest request =
                new UpdateProfileRequest("New Name");

        var response = userController.updateProfile(authentication, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("New Name", user.getName());

        verify(userRepository).save(user);
    }

    @Test
    void changePassword_ShouldChangePassword_WhenOldPasswordIsCorrect() {

        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("oldEncodedPassword")
                .roles(Set.of())
                .enabled(true)
                .accountLocked(false)
                .build();

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "oldPassword",
                "oldEncodedPassword"
        )).thenReturn(true);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("newEncodedPassword");

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "oldPassword",
                        "newPassword"
                );

        var response = userController.changePassword(authentication, request);

        assertEquals(200, response.getStatusCode().value());

        assertEquals(
                "newEncodedPassword",
                user.getPassword()
        );

        verify(userRepository).save(user);
    }

    @Test
    void changePassword_ShouldThrowBadRequest_WhenOldPasswordIsWrong() {

        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("oldEncodedPassword")
                .roles(Set.of())
                .enabled(true)
                .accountLocked(false)
                .build();

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(
                "wrongPassword",
                "oldEncodedPassword"
        )).thenReturn(false);

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "wrongPassword",
                        "newPassword"
                );

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> userController.changePassword(authentication, request)
        );

        assertEquals(
                "Old password is incorrect",
                exception.getMessage()
        );

        verify(userRepository, never()).save(user);
    }

    @Test
    void deleteAccount_ShouldDeleteCurrentUser() {

        User user = User.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .password("encoded")
                .roles(Set.of())
                .enabled(true)
                .accountLocked(false)
                .build();

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john@example.com");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(user));

        var response = userController.deleteAccount(authentication);

        assertEquals(200, response.getStatusCode().value());

        verify(userRepository).delete(user);
    }
}