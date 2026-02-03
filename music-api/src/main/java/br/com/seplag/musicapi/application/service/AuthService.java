package br.com.seplag.musicapi.application.service;

import br.com.seplag.musicapi.api.v1.dto.auth.LoginRequest;
import br.com.seplag.musicapi.api.v1.dto.auth.TokenResponse;
import br.com.seplag.musicapi.domain.model.AppUser;
import br.com.seplag.musicapi.domain.model.RefreshToken;
import br.com.seplag.musicapi.infrastructure.persistence.repository.RefreshTokenRepository;
import br.com.seplag.musicapi.infrastructure.persistence.repository.UserRepository;
import br.com.seplag.musicapi.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        AppUser user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!user.getEnabled()) {
            throw new BadCredentialsException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshTokenStr = jwtService.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenStr)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();

        refreshTokenRepository.save(refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(300L)
                .build();
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new BadCredentialsException("Refresh token has expired");
        }

        AppUser user = refreshToken.getUser();

        if (!user.getEnabled()) {
            throw new BadCredentialsException("User account is disabled");
        }

        // Delete old refresh token
        refreshTokenRepository.delete(refreshToken);

        // Generate new tokens
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String newRefreshTokenStr = jwtService.generateRefreshToken();

        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(user)
                .token(newRefreshTokenStr)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTtlSeconds()))
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshTokenStr)
                .tokenType("Bearer")
                .expiresIn(300L)
                .build();
    }
}
