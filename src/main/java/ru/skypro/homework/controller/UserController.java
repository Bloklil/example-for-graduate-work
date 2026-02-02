package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

/**
 * Контроллер для управления профилями пользователей.
 * Обрабатывает операции получения и обновления информации о пользователях,
 * включая изменение пароля и аватара.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(value = "http://localhost:3000")
public class UserController {

    private final UserService userService;

    @Operation(summary = "получить текущего user")
    @GetMapping("/me")
    public UserDto getUser() {
        return userService.getCurrentUser();
    }

    @Operation(summary = "обновить user")
    @PatchMapping("/me")
    public UserDto updateUser(@RequestBody UpdateUserDto dto) {
        return userService.updateUser(dto);
    }

    @Operation(summary = "обновить avatar")
    @PatchMapping(value = "/me/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateImage(@RequestParam("image") MultipartFile image) throws IOException {
        userService.updateUserImage(image);
    }

    @PostMapping("/me/password")
    public void updatePassword(@RequestBody NewPasswordDto dto) {
        userService.updatePassword(dto);
    }

}
