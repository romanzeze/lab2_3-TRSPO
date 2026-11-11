package com.example.lr23.dto;

import jakarta.validation.constraints.NotBlank;

/** Запит на створення або зміну лікаря чи медсестри. */
public record PersonRequest(@NotBlank(message = "name is required") String name, String profile) {
}
