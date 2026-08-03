package com.pulseai.questionbank.security;

import com.pulseai.questionbank.constants.SecurityConstants;
import com.pulseai.questionbank.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader(SecurityConstants.HEADER_STRING);
        String jwt = null;

        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            jwt = authHeader.substring(7);
        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt)) {
                String role = jwtUtil.extractClaim(jwt, claims -> claims.get("role", String.class));
                if (role != null && !role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                Object empIdObj = jwtUtil.extractClaim(jwt, claims -> claims.get("employeeId"));
                Long employeeId = empIdObj instanceof Number ? ((Number) empIdObj).longValue() : null;
                String region = jwtUtil.extractClaim(jwt, claims -> claims.get("region", String.class));

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        employeeId, null, Collections.singletonList(new SimpleGrantedAuthority(role)));
                
                // Store claims in request for controllers to access
                request.setAttribute("employeeId", employeeId);
                request.setAttribute("role", role);
                request.setAttribute("region", region);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
}


