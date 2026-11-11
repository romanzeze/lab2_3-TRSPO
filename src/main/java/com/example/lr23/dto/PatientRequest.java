package com.example.lr23.dto;

import jakarta.validation.constraints.NotBlank;

public record PatientRequest(@NotBlank(message = "name is required") String name) {
}
