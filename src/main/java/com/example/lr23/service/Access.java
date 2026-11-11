package com.example.lr23.service;

import com.example.lr23.exception.ForbiddenException;
import com.example.lr23.model.Role;

/** Перевірки прав за роллю з заголовка X-Role. */
final class Access {

    private Access() {
    }

    static void requireDoctor(Role role) {
        if (role != Role.DOCTOR) {
            throw new ForbiddenException("Only a doctor can perform this action");
        }
    }

    static void requireStaff(Role role) {
        if (role == null) {
            throw new ForbiddenException("The X-Role header must be DOCTOR or NURSE");
        }
    }
}
