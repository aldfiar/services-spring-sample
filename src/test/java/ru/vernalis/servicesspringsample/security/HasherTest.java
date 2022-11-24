package ru.vernalis.servicesspringsample.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HasherTest {
    private HasherService hasherService;
    @BeforeEach
    void setUp() {
        this.hasherService = new HasherService();
    }

    @Test
    public void testEncode(){
        String password = "abc123";
        String encoded = hasherService.encrypt(password);
        boolean result = hasherService.match(password, encoded);
        assertTrue(result, "Encoded password is not matched");
    }

}