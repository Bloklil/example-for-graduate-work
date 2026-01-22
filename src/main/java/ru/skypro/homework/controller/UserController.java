package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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
    @PatchMapping("/me/image")
    public void updateImage(@RequestParam MultipartFile image) throws IOException {
        userService.updateUserImage(image);
    }

}
