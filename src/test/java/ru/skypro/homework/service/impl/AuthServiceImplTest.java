package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsService userDetailsService;
    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_whenCredentialsCorrect_shouldReturnTrue() {
        String username = "user@example.com";
        String password = "password123";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getPassword()).thenReturn("encodedPassword123");

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // PasswordEncoder говорит, что пароли совпадают
        when(passwordEncoder.matches(password, "encodedPassword123")).thenReturn(true);

        boolean result = authService.login(username, password);

        assertTrue(result, "Логин должен быть успешным при правильных данных");

        // Метод loadUserByUsername вызвался РОВНО 1 раз
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        // Метод loadUserByUsername вызвался РОВНО 1 раз
        verify(passwordEncoder, times(1)).matches(password, "encodedPassword123");
    }

    @Test
    void login_whenPasswordWrong_shouldReturnFalse() {
        String username = "user@example.com";
        String wrongPassword = "wrongPassword";

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getPassword()).thenReturn("encodedPassword123");

        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        when(passwordEncoder.matches(wrongPassword, "encodedPassword123")).thenReturn(false);

        boolean result = authService.login(username, wrongPassword);

        assertFalse(result, "Логин должен провалиться при неправильном пароле");
    }

    @Test
    void login_whenUserNotFound_shouldReturnFalse() {
        String username = "unknown@example.com";
        String password = "password123";

        when(userDetailsService.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException("User not found"));

        boolean result = authService.login(username, password);

        assertFalse(result, "Логин должен провалиться, если пользователь не найден");

        // Проверяем, что PasswordEncoder НЕ вызывался
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void register_whenUserNotExists_shouldReturnTrue() {
        Register registerDto = new Register();
        registerDto.setUsername("newuser@example.com");
        registerDto.setPassword("password123");

        // Проверка пользователь НЕ существует
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("newuser@example.com");
        userEntity.setPassword("password123");

        when(userMapper.toEntity(registerDto)).thenReturn(userEntity);

        // PasswordEncoder кодирует пароль
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");

        when(userRepository.save(userEntity)).thenReturn(userEntity);

        boolean result = authService.register(registerDto);

        assertTrue(result, "Регистрация должна быть успешной");

        // Проверяем, что пароль действительно закодировался
        assertEquals("encodedPassword123", userEntity.getPassword(), "Пароль должен быть закодирован");

        // Проверяем вызовы
        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void register_whenUserAlreadyExists_shouldReturnFalse() {
        Register registerDto = new Register();
        registerDto.setUsername("existing@example.com");

        // Пользователь УЖЕ существует
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        boolean result = authService.register(registerDto);

        assertFalse(result, "Регистрация должна провалиться, если пользователь уже существует");

        // Проверяем, что дальше процесс не пошел
        verify(userMapper, never()).toEntity(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any());
    }
}