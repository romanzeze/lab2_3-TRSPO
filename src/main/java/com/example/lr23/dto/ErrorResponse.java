package com.example.lr23.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

/** Єдиний формат помилки: {"error": "...", "details": {...}}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String error, Map<String, String> details) {

    public ErrorResponse(String error) {
        this(error, null);
    }
}
