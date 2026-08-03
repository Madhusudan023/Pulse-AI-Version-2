package com.pulseai.authservice.controller;

import com.pulseai.authservice.dto.ChangePasswordRequest;
import com.pulseai.authservice.dto.CreateCredentialRequest;
import com.pulseai.authservice.dto.LoginRequest;
import com.pulseai.authservice.dto.LoginResponse;
import com.pulseai.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for login and token validation")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Authenticate with email and password")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/login")
    public ResponseEntity<com.pulseai.authservice.dto.ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(com.pulseai.authservice.dto.ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build());
    }

    @Operation(summary = "Create Credential", description = "Internal API to create auth credentials for employees")
    @Tag(name = "Internal APIs")
    @PostMapping("/create")
    public ResponseEntity<com.pulseai.authservice.dto.ApiResponse<Void>> createCredential(@RequestBody CreateCredentialRequest request) {
        authService.createCredential(request);
        return ResponseEntity.ok(com.pulseai.authservice.dto.ApiResponse.<Void>builder()
                .success(true)
                .message("Credential created successfully")
                .build());
    }

    @Operation(summary = "Change Password", description = "Change current user's password")
    @Tag(name = "Password APIs")
    @PostMapping("/change-password")
    public ResponseEntity<com.pulseai.authservice.dto.ApiResponse<Void>> changePassword(@RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(com.pulseai.authservice.dto.ApiResponse.<Void>builder()
                .success(true)
                .message("Password changed successfully")
                .build());
    }

    @Operation(summary = "Validate Token", description = "Internal API to validate JWT token")
    @Tag(name = "Internal APIs")
    @GetMapping("/validate")
    public ResponseEntity<com.pulseai.authservice.dto.ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = authService.validateToken(token);
            return ResponseEntity.ok(com.pulseai.authservice.dto.ApiResponse.<Boolean>builder()
                    .success(true)
                    .data(isValid)
                    .build());
        }
        return ResponseEntity.ok(com.pulseai.authservice.dto.ApiResponse.<Boolean>builder()
                .success(false)
                .data(false)
                .build());
    }

    @Operation(summary = "Get Current User", description = "Get authenticated user details")
    @GetMapping("/me")
    public ResponseEntity<com.pulseai.authservice.dto.ApiResponse<LoginResponse>> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        LoginResponse response = authService.getMe(token);
        return ResponseEntity.ok(com.pulseai.authservice.dto.ApiResponse.<LoginResponse>builder()
                .success(true)
                .data(response)
                .build());
    }
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
}
