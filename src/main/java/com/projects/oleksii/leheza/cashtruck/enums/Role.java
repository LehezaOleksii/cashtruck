package com.projects.oleksii.leheza.cashtruck.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    MANAGER,
    CLIENT;

    public String getAuthority() {
        return name();
    }
}
