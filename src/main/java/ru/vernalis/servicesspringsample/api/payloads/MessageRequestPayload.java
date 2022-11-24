package ru.vernalis.servicesspringsample.api.payloads;

import javax.validation.constraints.NotNull;

public class MessageRequestPayload {
    @NotNull
    private String name;
    @NotNull
    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
