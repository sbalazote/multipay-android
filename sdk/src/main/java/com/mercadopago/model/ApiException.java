package com.mercadopago.model;

import java.util.List;

public class ApiException {

    private List<Cause> cause;
    private String error;
    private String message;
    private Integer status;

    public ApiException() {
    }

    public ApiException(String message, Integer status, String error, List<Cause> cause) {

        this.message = message;
        this.status = status;
        this.error = error;
        this.cause = cause;
    }

    public List<Cause> getCause() {
        return cause;
    }

    public void setCause(List<Cause> cause) {
        this.cause = cause;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
