package ru.vernalis.servicesspringsample.api.payloads;

import javax.validation.constraints.NotNull;

public class AuthenticationRequest {
    @NotNull
    private String name;
    @NotNull
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
