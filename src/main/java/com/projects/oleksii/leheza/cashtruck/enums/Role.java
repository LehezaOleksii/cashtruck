package com.projects.oleksii.leheza.cashtruck.enums;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public enum Role implements GrantedAuthority {
    ROLE_ADMIN(1),
    ROLE_MANAGER(2),
    ROLE_CLIENT(3);

    private final int order;

    Role(int order) {
        this.order = order;
    }

    @Override
    public String getAuthority() {
        return this.name();
    }
}
