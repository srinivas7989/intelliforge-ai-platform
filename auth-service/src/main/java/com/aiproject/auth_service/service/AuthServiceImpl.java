package com.aiproject.auth_service.service;

import com.aiproject.auth_service.dto.*;
import com.aiproject.auth_service.entity.PasswordResetToken;
import com.aiproject.auth_service.entity.RefreshToken;
import com.aiproject.auth_service.entity.Role;
import com.aiproject.auth_service.entity.User;
import com.aiproject.auth_service.exception.BadRequestException;
import com.aiproject.auth_service.exception.ResourceNotFoundException;
import com.aiproject.auth_service.exception.UnauthorizedException;
import com.aiproject.auth_service.repository.PasswordResetTokenRepository;
import com.aiproject.auth_service.repository.RefreshTokenRepository;
import com.aiproject.auth_service.repository.RoleRepository;
import com.aiproject.auth_service.repository.UserRepository;
import com.aiproject.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already registered");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name("USER")
                                .build()
                ));

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .enabled(true)
                .accountLocked(false)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());

        String accessToken = jwtService.generateToken(userDetails);

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RefreshToken refreshToken = createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                900
        );
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(
                refreshToken.getUser().getEmail()
        );

        String accessToken = jwtService.generateToken(userDetails);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                900
        );
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Map<String, String> forgotPassword(ForgotPasswordRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(15 * 60))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        return Map.of(
                "message", "Password reset token generated",
                "resetToken", token
        );
    }

    @Override
    public Map<String, String> resetPassword(ResetPasswordRequest request) {

        PasswordResetToken token = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new BadRequestException("Invalid reset token"));

        if (token.isUsed()) {
            throw new BadRequestException("Reset token already used");
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new BadRequestException("Reset token expired");
        }

        User user = token.getUser();

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);

        token.setUsed(true);

        passwordResetTokenRepository.save(token);

        return Map.of(
                "message", "Password reset successfully"
        );
    }

    private RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }
}