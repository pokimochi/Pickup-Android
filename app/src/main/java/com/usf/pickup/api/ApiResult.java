package com.usf.pickup.api;

/**
 * A generic class that holds a result success w/ data or an error.
 */
public class ApiResult<T> {
    private T data;
    private String errorMessage;

    private ApiResult(T data, String errorMessage) {
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public static <T> ApiResult<T> Success(T data){
        return new ApiResult<>(data, null);
    }

    public static <T> ApiResult<T> Error(String errorMessage){
        return new ApiResult<>(null, errorMessage);
    }

    public T getData() {
        return data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess(){
        return data != null;
    }

    public interface Listener<T> {
        /** Called when a response is received. */
        void onResponse(ApiResult<T> response);
    }
}
