package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;

import java.io.IOException;

public interface UserService {
    UserDto getCurrentUser();
    UserDto updateUser(UpdateUserDto updateUserDto);
    void updatePassword(NewPasswordDto newPasswordDto);
    void updateUserImage(MultipartFile image) throws IOException;
}
