package com.projects.oleksii.leheza.cashtruck.enums;

import lombok.Getter;

@Getter
public enum Role {
    ADMIN(1),
    MANAGER(2),
    CLIENT(3);

    private final int order;

    Role(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        String roleName = name().toLowerCase();
        return Character.toUpperCase(roleName.charAt(0)) + roleName.substring(1);
    }
}
