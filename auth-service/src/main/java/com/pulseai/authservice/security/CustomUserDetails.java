package com.pulseai.authservice.security;

import com.pulseai.authservice.entity.UserCredential;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final UserCredential userCredential;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(userCredential.getRole()));
    }

    @Override
    public String getPassword() {
        return userCredential.getPassword();
    }

    @Override
    public String getUsername() {
        return userCredential.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !userCredential.isAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return userCredential.isActive();
    }
    public CustomUserDetails(UserCredential userCredential) {
        this.userCredential = userCredential;
    }
    public UserCredential getUserCredential() { return this.userCredential; }
}
