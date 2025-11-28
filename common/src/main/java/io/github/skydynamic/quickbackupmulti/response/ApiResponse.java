package io.github.skydynamic.quickbackupmulti.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("unused")
public class ApiResponse<T> {
    private int statusCode;
    private T message;

    public ApiResponse(ApiStatusCode status, T message) {
        this.statusCode = status.getCode();
        this.message = message;
    }

    public ApiResponse(T message) {
        this.statusCode = ApiStatusCode.SUCCESS.getCode();
        this.message = message;
    }
}
