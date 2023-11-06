package com.alexandervov.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.alexandervov.service.Constants.ROLE_DEVELOPER;

@Service
public class UserService {

    public String getUserName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public boolean isDeveloper() {
        final Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext()
            .getAuthentication()
            .getAuthorities();
        return authorities.contains(ROLE_DEVELOPER);
    }
}
