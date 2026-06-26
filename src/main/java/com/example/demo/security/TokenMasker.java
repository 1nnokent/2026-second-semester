package com.example.demo.security;

public final class TokenMasker {

    private TokenMasker() {
    }

    public static String mask(String token) {
        if (token == null || token.isBlank()) {
            return "<empty>";
        }

        if (token.length() <= 12) {
            return token.charAt(0) + "***" + token.charAt(token.length() - 1);
        }

        return token.substring(0, 6) + "..." + token.substring(token.length() - 6);
    }
}
