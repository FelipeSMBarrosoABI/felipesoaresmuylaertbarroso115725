package br.com.seplag.musicapi.application.service;

import br.com.seplag.musicapi.api.v1.dto.auth.LoginRequest;
import br.com.seplag.musicapi.api.v1.dto.auth.TokenResponse;
import br.com.seplag.musicapi.domain.model.AppUser;
import br.com.seplag.musicapi.domain.model.RefreshToken;
import br.com.seplag.musicapi.infrastructure.persistence.repository.RefreshTokenRepository;
import br.com.seplag.musicapi.infrastructure.persistence.repository.UserRepository;
import br.com.seplag.musicapi.infrastructure.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private AppUser testUser;

    @BeforeEach
    void setUp() {
        testUser = AppUser.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("hashedPassword")
                .enabled(true)
                .build();
    }

    @Test
    void login_WithValidCredentials_ReturnsTokenResponse() {
        LoginRequest request = new LoginRequest("testuser", "password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtService.generateAccessToken("testuser")).thenReturn("access-token");
        when(jwtService.generateRefreshToken()).thenReturn("refresh-token");
        when(jwtService.getRefreshTtlSeconds()).thenReturn(86400L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        TokenResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void login_WithInvalidUsername_ThrowsBadCredentialsException() {
        LoginRequest request = new LoginRequest("nonexistent", "password123");

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void login_WithInvalidPassword_ThrowsBadCredentialsException() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    void login_WithDisabledUser_ThrowsBadCredentialsException() {
        testUser.setEnabled(false);
        LoginRequest request = new LoginRequest("testuser", "password123");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("User account is disabled");
    }

    @Test
    void refresh_WithValidToken_ReturnsNewTokenResponse() {
        RefreshToken refreshToken = RefreshToken.builder()
                .id(1L)
                .user(testUser)
                .token("valid-refresh-token")
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(refreshTokenRepository.findByToken("valid-refresh-token")).thenReturn(Optional.of(refreshToken));
        when(jwtService.generateAccessToken("testuser")).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken()).thenReturn("new-refresh-token");
        when(jwtService.getRefreshTtlSeconds()).thenReturn(86400L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArgument(0));

        TokenResponse response = authService.refresh("valid-refresh-token");

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
        verify(refreshTokenRepository).delete(refreshToken);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void refresh_WithInvalidToken_ThrowsBadCredentialsException() {
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.refresh("invalid-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void refresh_WithExpiredToken_ThrowsBadCredentialsException() {
        RefreshToken expiredToken = RefreshToken.builder()
                .id(1L)
                .user(testUser)
                .token("expired-token")
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> authService.refresh("expired-token"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Refresh token has expired");

        verify(refreshTokenRepository).delete(expiredToken);
    }
}
