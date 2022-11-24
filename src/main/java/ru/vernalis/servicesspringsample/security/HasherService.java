package ru.vernalis.servicesspringsample.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Хэширование паролей для хранения в ЮД
 */
@Component
public final class HasherService {
    private final BCryptPasswordEncoder encoder;

    public HasherService() {
        this.encoder = new BCryptPasswordEncoder();
    }

    public String encrypt(String password) {
        return encoder.encode(password);
    }

    public boolean match(String rawPassword, String encoded) {
        return this.encoder.matches(rawPassword, encoded);
    }

}
