package com.msrts.hostel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msrts.hostel.constant.Role;
import com.msrts.hostel.entity.Token;
import com.msrts.hostel.entity.TokenType;
import com.msrts.hostel.entity.User;
import com.msrts.hostel.exception.ErrorConstants;
import com.msrts.hostel.model.AuthenticationRequest;
import com.msrts.hostel.model.AuthenticationResponse;
import com.msrts.hostel.model.RefreshToken;
import com.msrts.hostel.model.RegisterRequest;
import com.msrts.hostel.repository.ReferralSequenceRepository;
import com.msrts.hostel.repository.TokenRepository;
import com.msrts.hostel.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final TokenRepository tokenRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Autowired
    private final ReferralSequenceRepository hostelSequenceRepository;

    public AuthenticationResponse register(RegisterRequest request, boolean isAppDefaultUser) {
        if (request != null && request.getUsername() != null) {
            User user = userRepository.findByUsername(request.getUsername());
            if (user != null) {
                if (isAppDefaultUser)
                    return new AuthenticationResponse(ErrorConstants.ERROR_USER_EXISTS, null, null);
                else
                    throw new RuntimeException(ErrorConstants.ERROR_USER_EXISTS);
            }
        }

        Long currentSequence = hostelSequenceRepository.getSequenceByFirstAndLastName(
                request.getFirstname().trim().substring(0, 2).toUpperCase(),
                request.getLastname().trim().substring(0, 2).toUpperCase()
        );
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .username(request.getUsername())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .role((request.getRole() != null) ? request.getRole() : Role.USER)
                .referralCode(generateSequence(request, currentSequence))
                .referredByCode(request.getReferredByCode())
                .points(100)
                .build();

        var savedUser = userRepository.save(user);
        updateReferredUserCoins(user.getReferredByCode());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void updateReferredUserCoins(String referredByCode) {
        User referredUser = userRepository.findByReferralCode(referredByCode);
        if(referredUser != null) {
            referredUser.setPoints(referredUser.getPoints() + 50);
            userRepository.save(referredUser);
        }
    }

    private String generateSequence(RegisterRequest user, Long currentSequence) {
        if (currentSequence != null && currentSequence > 0) {
            return user.getFirstname().trim().substring(0, 2).toUpperCase()
                    + user.getLastname().trim().substring(0, 2).toUpperCase()
                    + (currentSequence + 1);
        } else {
            return user.getFirstname().trim().substring(0, 2).toUpperCase()
                    + user.getLastname().trim().substring(0, 2).toUpperCase()
                    + 1;
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.userRepository.findByEmail(userEmail);
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(new RefreshToken(1, user.getEmail(), refreshToken, 2,
                                new Date(System.currentTimeMillis() + refreshExpiration)))
                        .build();
                objectMapper.writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
