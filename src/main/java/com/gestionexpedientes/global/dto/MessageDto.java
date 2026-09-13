package com.gestionexpedientes.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {

    private HttpStatus status;
    private String message;
    private String code;

    public MessageDto() {
    }

    public MessageDto(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public MessageDto(HttpStatus status, String message, String code) {
        this(status, message);
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
