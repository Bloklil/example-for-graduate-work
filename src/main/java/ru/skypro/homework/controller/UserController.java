package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;

@RestController
@RequestMapping("/users")
public class UserController {

    @Operation(summary = "получить текущего user")
    @GetMapping("/me")
    public UserDto getUser() {
        return new UserDto();
    }

    @Operation(summary = "обновить user")
    @PatchMapping("/me")
    public UpdateUserDto updateUser(@RequestBody UpdateUserDto dto) {
        return dto;
    }

    @Operation(summary = "обновить avatar")
    @PatchMapping("/me/image")
    public void updateImage(@RequestParam MultipartFile image) {
    }

}
