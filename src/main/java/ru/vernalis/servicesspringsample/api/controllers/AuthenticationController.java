package ru.vernalis.servicesspringsample.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.vernalis.servicesspringsample.api.payloads.AuthenticationRequest;
import ru.vernalis.servicesspringsample.api.payloads.AuthenticationResponse;
import ru.vernalis.servicesspringsample.persistance.model.ServiceUser;
import ru.vernalis.servicesspringsample.persistance.model.ServiceUserRepository;
import ru.vernalis.servicesspringsample.security.HasherService;
import ru.vernalis.servicesspringsample.security.JwtService;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Контроллер, отвечающий за регистрация и выдачу токенов зарегистрированным пользователям
 */
@RestController
@RequestMapping("/api")

public class AuthenticationController {
    private final JwtService jwtService;
    private final ServiceUserRepository serviceUserRepository;
    private final HasherService hasherService;


    public AuthenticationController(JwtService jwtService, ServiceUserRepository serviceUserRepository, HasherService hasherService) {
        this.jwtService = jwtService;
        this.serviceUserRepository = serviceUserRepository;
        this.hasherService = hasherService;
    }

    /**
     * Эндпоинт для получения токена
     *
     * @param request запрос с данными пользователя
     */
    @PostMapping(path = "/token", produces = APPLICATION_JSON_VALUE)
    public AuthenticationResponse createAuthenticationToken(@Valid @RequestBody AuthenticationRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        ServiceUser user = serviceUserRepository.findUserByName(request.getName());
        if (user == null || !hasherService.match(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String token = jwtService.generateToken(user.getName());

        return new AuthenticationResponse(token);
    }

    /**
     * Эндпоинт для регистрации нового пользователя
     *
     * @param request запрос с данными пользователя
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity createUser(@Valid @RequestBody AuthenticationRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (serviceUserRepository.findUserByName(request.getName()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already exists");
        }

        String password = hasherService.encrypt(request.getPassword());
        ServiceUser user = new ServiceUser();
        user.setName(request.getName());
        user.setPassword(password);

        serviceUserRepository.save(user);

        return ResponseEntity.ok().build();
    }

}
