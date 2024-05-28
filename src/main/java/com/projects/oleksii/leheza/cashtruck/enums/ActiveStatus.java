package com.projects.oleksii.leheza.cashtruck.enums;

public enum ActiveStatus {
    ACTIVE,
    INACTIVE,
    BANNED;

    public static boolean isEnabled(ActiveStatus status) {
        return status == ACTIVE;
    }
}
