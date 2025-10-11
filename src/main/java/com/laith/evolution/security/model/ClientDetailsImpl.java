package com.laith.evolution.security.model;


import com.laith.evolution.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
@RequiredArgsConstructor
public final class ClientDetailsImpl implements UserDetails {

    private final Client client;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return client.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();
    }

    @Override
    public String getPassword() {
        return client.getClientSecret();
    }

    @Override
    public String getUsername() {
        return client.getClientId();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
