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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserCredentialRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private UserCredential userCredential;

    @BeforeEach
    void setUp() {
        userCredential = new UserCredential();
        userCredential.setEmail("test@example.com");
        userCredential.setPassword("encodedHash");
        userCredential.setEmployeeId(1L);
        userCredential.setRole("ROLE_EMPLOYEE");
        userCredential.setRegion("PUNE");
        userCredential.setDepartment("ENGINEERING");
        userCredential.setActive(true);
    }

    // --- 1. createCredential Tests ---

    @Test
    void createCredential_Success() {
        CreateCredentialRequest request = new CreateCredentialRequest();
        request.setEmail("test@example.com");
        request.setPassword("plainText");
        request.setEmployeeId(1L);
        request.setRole("ROLE_EMPLOYEE");
        request.setRegion("PUNE");
        request.setDepartment("ENGINEERING");

        when(repository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainText")).thenReturn("encodedHash");

        authService.createCredential(request);

        verify(repository, times(1)).save(any(UserCredential.class));
    }

    @Test
    void createCredential_DuplicateEmail_ThrowsBusinessException() {
        CreateCredentialRequest request = new CreateCredentialRequest();
        request.setEmail("test@example.com");

        when(repository.existsByEmail("test@example.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.createCredential(request);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(repository, never()).save(any(UserCredential.class));
    }

    // --- 2. login Tests ---

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("plainText");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(userCredential));
        when(jwtUtil.generateToken("test@example.com", 1L, "ROLE_EMPLOYEE", "PUNE", "ENGINEERING")).thenReturn("mockJwtToken");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mockJwtToken", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        verify(repository, times(1)).save(userCredential);
    }

    @Test
    void login_InvalidCredentials_ThrowsUnauthorizedException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.login(request);
        });

        assertEquals("Invalid email or password", exception.getMessage());
        verify(repository, never()).findByEmail(anyString());
    }

    @Test
    void login_UserNotFoundAfterAuth_ThrowsUnauthorizedException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("plainText");
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> {
            authService.login(request);
        });

        assertEquals("User not found", exception.getMessage());
    }

    // --- 3. changePassword Tests ---

    @Test
    void changePassword_Success() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("test@example.com");
        request.setOldPassword("oldPlain");
        request.setNewPassword("newPlain");

        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(userCredential));
        when(passwordEncoder.matches("oldPlain", "encodedHash")).thenReturn(true);
        when(passwordEncoder.encode("newPlain")).thenReturn("newHash");

        authService.changePassword(request);

        verify(repository, times(1)).save(userCredential);
        assertEquals("newHash", userCredential.getPassword());
    }

    @Test
    void changePassword_WrongOldPassword_ThrowsBusinessException() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("test@example.com");
        request.setOldPassword("wrongOldPlain");
        request.setNewPassword("newPlain");

        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(userCredential));
        when(passwordEncoder.matches("wrongOldPlain", "encodedHash")).thenReturn(false);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.changePassword(request);
        });

        assertEquals("Old password does not match", exception.getMessage());
        verify(repository, never()).save(any(UserCredential.class));
    }

    // --- 4. validateToken Tests ---

    @Test
    void validateToken_Success() {
        String token = "validToken";
        when(jwtUtil.extractEmail(token)).thenReturn("test@example.com");
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(userCredential));
        when(jwtUtil.validateToken(token, "test@example.com")).thenReturn(true);

        boolean isValid = authService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ExpiredOrMalformedToken_ReturnsFalse() {
        String token = "expiredToken";
        when(jwtUtil.extractEmail(token)).thenThrow(new RuntimeException("Expired"));

        boolean isValid = authService.validateToken(token);

        assertFalse(isValid);
    }

    @Test
    void validateToken_UserInactive_ReturnsFalse() {
        String token = "validToken";
        userCredential.setActive(false);
        when(jwtUtil.extractEmail(token)).thenReturn("test@example.com");
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(userCredential));

        boolean isValid = authService.validateToken(token);

        assertFalse(isValid);
    }
}
