package com.projects.oleksii.leheza.cashtruck.enums;

public enum ActiveStatus {
    ACTIVE,
    INACTIVE,
    BANNED;

    public static boolean isEnabled(ActiveStatus status) {
        return status == ACTIVE;
    }

    @Override
    public String toString() {
        String roleName = name().toLowerCase();
        return Character.toUpperCase(roleName.charAt(0)) + roleName.substring(1);
    }
}
