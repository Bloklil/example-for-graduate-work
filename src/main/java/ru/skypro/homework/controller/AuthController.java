package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.build.Plugin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.IdDto;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.repositories.UserRepository;
import ru.skypro.homework.service.AuthService;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @Operation(summary = "Авторизация пользователя")
    @ApiResponse(responseCode = "200", description = "успешная авторизация")
    @ApiResponse(responseCode = "401", description = "не фортануло")
    @PostMapping("/login")
    public IdDto login(@RequestBody Login login) {
        boolean success = authService.login(login.getUsername(), login.getPassword());
        if (!success) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        UserEntity user = userRepository.findByEmail(login.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не найден"));
        return new IdDto(user.getId());
    }

    @Operation(summary = "регистрация пользователя")
    @ApiResponse(responseCode = "201", description = "пользоватьель зарегистрирован")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDto register(@RequestBody Register register) {
        boolean success = authService.register(register);

        if (!success) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        UserEntity user = userRepository.findByEmail(register.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не найден"));
        return new IdDto(user.getId());
    }
}
