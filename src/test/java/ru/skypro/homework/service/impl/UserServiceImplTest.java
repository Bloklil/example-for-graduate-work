package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repositories.UserRepository;
import ru.skypro.homework.service.FileService;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private FileService fileService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1);
        testUser.setEmail("user@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");
        testUser.setPhone("+79788125640");
        testUser.setImage("old-avatar.jpg");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupCurrentUser(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    @Test
    void getCurrentUser_shouldReturnUserDto() {
        setupCurrentUser("user@example.com");

        UserDto expectedDto = new UserDto();
        expectedDto.setId(1);
        expectedDto.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(expectedDto);

        UserDto result = userService.getCurrentUser();

        // Проверяем, что результат не null
        assertNotNull(result);
        // Проверяем ID
        assertEquals(1, result.getId());
        // Проверяем email
        assertEquals("user@example.com", result.getEmail());
    }

    @Test
    void updatePassword_whenPasswordCorrect_shouldUpdate() {
        setupCurrentUser("user@example.com");

        NewPasswordDto passwordDto = new NewPasswordDto();
        passwordDto.setCurrentPassword("oldPassword");
        passwordDto.setNewPassword("newPassword");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        userService.updatePassword(passwordDto);

        // Проверяем, что пароль изменился
        assertEquals("newEncodedPassword", testUser.getPassword());
        // Проверяем, что пользователь сохранился
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserImage_shouldUpdateAvatar() throws IOException {
        setupCurrentUser("user@example.com");

        // Создаем "поддельный" файл картинки
        MockMultipartFile newImage = new MockMultipartFile(
                "image",
                "avatar.jpg",
                "image/jpeg",
                "content".getBytes());

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));
        when(fileService.saveImage(newImage)).thenReturn("new-avatar.jpg");

        userService.updateUserImage(newImage);

        // Старая картинка должна удалиться
        verify(fileService).deleteImage("old-avatar.jpg");
        // Новая картинка должна сохраниться
        verify(fileService).saveImage(newImage);
        // Имя файла должно обновиться у пользователя
        assertEquals("new-avatar.jpg", testUser.getImage());
        // Пользователь должен сохраниться
        verify(userRepository).save(testUser);
    }

    @Test
    void updatePassword_whenPasswordWrong_shouldThrowException() {
        setupCurrentUser("user@example.com");

        NewPasswordDto passwordDto = new NewPasswordDto();
        passwordDto.setCurrentPassword("wrongPassword");  // НЕВЕРНЫЙ пароль!
        passwordDto.setNewPassword("newPassword");

        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(testUser));

        // Пароль НЕ совпадает → false
        when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        // Ожидаем, что будет выброшено исключение
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updatePassword(passwordDto);
        });

        // Дополнительная проверка: пароль НЕ должен измениться
        assertEquals("encodedPassword", testUser.getPassword(),
                "Пароль не должен меняться при ошибке");

        // Дополнительная проверка: save() НЕ должен вызываться
        verify(userRepository, never()).save(any());
    }
}