package com.deqode.financeresearch.service;

import com.deqode.financeresearch.dto.AuthResponse;
import com.deqode.financeresearch.dto.LoginRequest;
import com.deqode.financeresearch.dto.RegisterRequest;
import com.deqode.financeresearch.dto.UserDto;
import com.deqode.financeresearch.entity.Session;
import com.deqode.financeresearch.entity.User;
import com.deqode.financeresearch.repository.SessionRepository;
import com.deqode.financeresearch.repository.UserRepository;
import com.deqode.financeresearch.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SessionRepository sessionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        logger.info("New user registered: {}", savedUser.getEmail());
        
        // Generate tokens
        String accessToken = tokenProvider.generateToken(savedUser.getEmail(), savedUser.getId());
        String refreshToken = tokenProvider.generateRefreshToken(savedUser.getEmail(), savedUser.getId());
        
        // Create session
        createSession(savedUser, accessToken);
        
        UserDto userDto = UserDto.fromEntity(savedUser);
        return new AuthResponse(accessToken, refreshToken, userDto);
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            // Get user details
            User user = userRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Generate tokens
            String accessToken = tokenProvider.generateToken(user.getEmail(), user.getId());
            String refreshToken = tokenProvider.generateRefreshToken(user.getEmail(), user.getId());
            
            // Create session
            createSession(user, accessToken);
            
            logger.info("User logged in: {}", user.getEmail());
            
            UserDto userDto = UserDto.fromEntity(user);
            return new AuthResponse(accessToken, refreshToken, userDto);
            
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", request.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
    }
    
    public void logout(String token) {
        try {
            String email = tokenProvider.getEmailFromToken(token);
            Optional<Session> session = sessionRepository.findBySessionToken(token);
            
            if (session.isPresent()) {
                session.get().setIsRevoked(true);
                sessionRepository.save(session.get());
                logger.info("User logged out: {}", email);
            }
        } catch (Exception e) {
            logger.error("Error during logout", e);
        }
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String email = tokenProvider.getEmailFromToken(refreshToken);
        Long userId = tokenProvider.getUserIdFromToken(refreshToken);
        
        User user = userRepository.findActiveUserByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate new access token
        String newAccessToken = tokenProvider.generateToken(email, userId);
        String newRefreshToken = tokenProvider.generateRefreshToken(email, userId);
        
        // Create new session
        createSession(user, newAccessToken);
        
        UserDto userDto = UserDto.fromEntity(user);
        return new AuthResponse(newAccessToken, newRefreshToken, userDto);
    }
    
    public boolean validateToken(String token) {
        if (!tokenProvider.validateToken(token)) {
            return false;
        }
        
        Optional<Session> session = sessionRepository.findValidSessionByToken(token, LocalDateTime.now());
        return session.isPresent();
    }
    
    public void revokeAllUserSessions(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        sessionRepository.revokeAllUserSessions(user);
    }
    
    private void createSession(User user, String token) {
        LocalDateTime expiresAt = tokenProvider.getExpirationFromToken(token);
        Session session = new Session(user, token, expiresAt);
        sessionRepository.save(session);
    }
    
    // Cleanup expired sessions (should be called periodically)
    public void cleanupExpiredSessions() {
        sessionRepository.revokeExpiredSessions(LocalDateTime.now());
        sessionRepository.deleteOldSessions(LocalDateTime.now().minusDays(30));
    }
}