package io.github.skydynamic.quickbackupmulti.response;

import lombok.Getter;

public enum ApiStatusCode {
    SUCCESS(0),
    // Server error code 1-999
    SERVER_NOT_STARTED(1),
    BACKUP_NOT_FOUND(21),
    BACKUP_NAME_IS_NULL(22),
    BACKUP_EXISTS(23),
    // User error code 1000-1999
    USERNAME_OR_PASSWORD_EMPTY(1001),
    USERNAME_OR_PASSWORD_INVALID(1005),;

    @Getter
    private final int code;

    ApiStatusCode(int code) {
        this.code = code;
    }
}
