package com.movieweb.movieweb.common.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

public final class Generator {

    private Generator() {}
    public static String generateSlug(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String slug = normalized
                .toLowerCase()
                .replaceAll("[^\\w\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        return slug;
    }

    public static String generateId() {
        return generateId(8);
    }

    public static String generateId(int length) {
        final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        return NanoIdUtils.randomNanoId(
                NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
                ALPHANUMERIC.toCharArray(),
                length
        );
    }

}
