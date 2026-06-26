package com.example.demo.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PepperingPasswordEncoder implements PasswordEncoder {

    private final PasswordEncoder delegate;
    private final String pepper;

    public PepperingPasswordEncoder(String pepper) {
        this.delegate = new BCryptPasswordEncoder();
        this.pepper = pepper;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return delegate.encode(withPepper(rawPassword));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return delegate.matches(withPepper(rawPassword), encodedPassword);
    }

    private String withPepper(CharSequence rawPassword) {
        return rawPassword + pepper;
    }
}
