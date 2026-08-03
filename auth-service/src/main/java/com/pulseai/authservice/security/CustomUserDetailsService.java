package com.pulseai.authservice.security;

import com.pulseai.authservice.entity.UserCredential;
import com.pulseai.authservice.repository.UserCredentialRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserCredentialRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserCredential user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return new CustomUserDetails(user);
    }
    public CustomUserDetailsService(UserCredentialRepository repository) {
        this.repository = repository;
    }
}
