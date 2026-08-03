package com.pulseai.authservice.service;

import com.pulseai.authservice.dto.ChangePasswordRequest;
import com.pulseai.authservice.dto.CreateCredentialRequest;
import com.pulseai.authservice.dto.LoginRequest;
import com.pulseai.authservice.dto.LoginResponse;
import com.pulseai.authservice.entity.UserCredential;
import com.pulseai.authservice.exception.BusinessException;
import com.pulseai.authservice.exception.UnauthorizedException;
import com.pulseai.authservice.repository.UserCredentialRepository;
import com.pulseai.authservice.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserCredentialRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public void createCredential(CreateCredentialRequest request) {
        log.info("Creating credential for email: {}", request.getEmail());
        if (repository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        UserCredential credential = new UserCredential();
        credential.setEmployeeId(request.getEmployeeId());
        credential.setEmail(request.getEmail());
        credential.setPassword(passwordEncoder.encode(request.getPassword()));
        credential.setRole(request.getRole());
        credential.setRegion(request.getRegion());
        credential.setDepartment(request.getDepartment());
        
        repository.save(credential);
        log.info("Credential successfully created for email: {}", request.getEmail());
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            log.error("Invalid credentials for email: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        UserCredential user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        repository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getEmployeeId(), user.getRole(), user.getRegion(), user.getDepartment());
        
        log.info("Login successful for email: {}", request.getEmail());
        return LoginResponse.builder()
                .token(token)
                .employeeId(user.getEmployeeId())
                .email(user.getEmail())
                .role(user.getRole())
                .region(user.getRegion())
                .build();
    }

    public void changePassword(ChangePasswordRequest request) {
        UserCredential user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Old password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        repository.save(user);
        log.info("Password changed successfully for email: {}", request.getEmail());
    }

    public boolean validateToken(String token) {
        try {
            String email = jwtUtil.extractEmail(token);
            UserCredential user = repository.findByEmail(email).orElse(null);
            if (user == null || !user.isActive()) {
                return false;
            }
            return jwtUtil.validateToken(token, email);
        } catch (Exception e) {
            return false;
        }
    }

    public LoginResponse getMe(String token) {
        String email = jwtUtil.extractEmail(token);
        UserCredential user = repository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
        return LoginResponse.builder()
                .token(token)
                .employeeId(user.getEmployeeId())
                .email(user.getEmail())
                .role(user.getRole())
                .region(user.getRegion())
                .build();
    }
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthService.class);
    public AuthService(UserCredentialRepository repository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }
}
