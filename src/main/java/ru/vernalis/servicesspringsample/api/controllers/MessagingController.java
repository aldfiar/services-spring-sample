package ru.vernalis.servicesspringsample.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vernalis.servicesspringsample.api.payloads.HistoryRequestPayload;
import ru.vernalis.servicesspringsample.api.payloads.HistoryResponsePayload;
import ru.vernalis.servicesspringsample.api.payloads.MessageRequestPayload;
import ru.vernalis.servicesspringsample.persistance.model.ServiceUser;
import ru.vernalis.servicesspringsample.persistance.model.ServiceUserRepository;
import ru.vernalis.servicesspringsample.persistance.model.UserMessage;
import ru.vernalis.servicesspringsample.persistance.model.UserMessageRepository;
import ru.vernalis.servicesspringsample.security.JwtService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер, отвечающий за прием сообщений и получения истории
 */
@RestController
@RequestMapping("/message")
public class MessagingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingController.class);
    private final JwtService jwtService;
    private final UserMessageRepository userMessageRepository;

    private final ServiceUserRepository serviceUserRepository;

    public MessagingController(JwtService jwtService, UserMessageRepository userMessageRepository, ServiceUserRepository serviceUserRepository) {
        this.jwtService = jwtService;
        this.userMessageRepository = userMessageRepository;
        this.serviceUserRepository = serviceUserRepository;
    }

    /**
     * Эндпоинт для отправки сообщения
     *
     * @param request запрос с данными сообщения
     */
    @PostMapping(path = "send", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity sendMessage(@Valid @RequestBody MessageRequestPayload request, @RequestHeader("Authorization") String auth, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!auth.startsWith("Bearer")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String name = request.getName();

        String tokenPart = extractToken(auth);
        Optional<String> sender = jwtService.getSender(tokenPart);
        if (!sender.isPresent() || !sender.get().equals(name)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        ServiceUser userByName = serviceUserRepository.findUserByName(name);

        UserMessage userMessage = new UserMessage();
        userMessage.setUser(userByName);
        userMessage.setText(request.getMessage());

        userMessageRepository.save(userMessage);

        return ResponseEntity.ok().build();
    }

    private String extractToken(String tokenLine) {
        return tokenLine.replace("Bearer_", "");
    }

    /**
     * Эндпоинт для получения истории
     *
     * @param request запрос с параметрами для получения истории
     */
    @PostMapping(path = "history", produces = APPLICATION_JSON_VALUE)
    public HistoryResponsePayload getHistory(@Valid @RequestBody HistoryRequestPayload request, @RequestHeader("Authorization") String auth, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (!auth.startsWith("Bearer")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        String name = request.getName();

        String tokenPart = extractToken(auth);
        Optional<String> sender = jwtService.getSender(tokenPart);
        if (!sender.isPresent() || !sender.get().equals(name)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String historyRequest = request.getMessage();
        if (!historyRequest.startsWith("history")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String history = historyRequest.replace("history", "").trim();
        int historyCount = 0;
        try {
            historyCount = Integer.parseInt(history);
        } catch (NumberFormatException ex) {
            LOGGER.error("Not valid history request: {}", historyRequest);
        }
        if (historyCount == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong body");
        }

        ServiceUser userByName = serviceUserRepository.findUserByName(name);
        List<UserMessage> userMessages = userMessageRepository.getUserMessages(userByName.getId(), historyCount);
        List<String> historyData = userMessages.stream().map(UserMessage::getText).collect(Collectors.toList());
        HistoryResponsePayload historyResponsePayload = new HistoryResponsePayload();
        historyResponsePayload.setHistory(historyData);

        return historyResponsePayload;
    }
}
