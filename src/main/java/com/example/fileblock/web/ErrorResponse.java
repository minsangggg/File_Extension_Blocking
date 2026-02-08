package com.example.fileblock.web;

public record ErrorResponse(String code, String message) {
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message);
    }
}
