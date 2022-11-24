package ru.vernalis.servicesspringsample.api.payloads;

import java.util.List;

public class HistoryResponsePayload {
    private List<String> history;

    public List<String> getHistory() {
        return history;
    }

    public void setHistory(List<String> history) {
        this.history = history;
    }
}
