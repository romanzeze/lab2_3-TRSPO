package com.example.lr23.model;

/** Роль користувача, що виконує дію. Передається в заголовку X-Role. */
public enum Role {
    DOCTOR,
    NURSE;

    /** Повертає роль за текстом (без урахування регістру) або null, якщо роль не вказана чи невідома. */
    public static Role parse(String value) {
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
