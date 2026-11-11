package com.example.lr23.model;

public enum PrescriptionType {
    PROCEDURE,
    MEDICINE,
    OPERATION;

    /** Повертає тип за текстом (без урахування регістру) або null, якщо значення некоректне. */
    public static PrescriptionType parse(String value) {
        if (value == null) {
            return null;
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
