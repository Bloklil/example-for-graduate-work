package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repositories.UserRepository;
import ru.skypro.homework.service.FileService;
import ru.skypro.homework.service.UserService;

import java.io.IOException;

/**
 * Реализация сервиса для управления профилями пользователей.
 * Обрабатывает бизнес-логику работы с пользователями, включая проверку прав доступа.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;

    /**
     * Получает текущего аутентифицированного пользователя из контекста безопасности.
     *
     * @return сущность текущего пользователя
     * @throws UserNotFoundException если пользователь не найден в базе данных
     */
    private UserEntity getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

    /**
     * {@inheritDoc}
     * Требует аутентификации пользователя.
     */
    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("isAuthenticated()")
    public UserDto getCurrentUser() {
        UserEntity userEntity = getCurrentUserEntity();
        return userMapper.toDto(userEntity);
    }

    /**
     * {@inheritDoc}
     * Требует аутентификации пользователя.
     * Пользователь может обновлять только свой собственный профиль.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public UserDto updateUser(UpdateUserDto updateUserDto) {
        UserEntity userEntity = getCurrentUserEntity();

        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(userEntity.getEmail())) {
            throw new AccessDeniedException("You can only update your own profile");
        }

        userMapper.updateEntityFromDto(userEntity, updateUserDto);
        UserEntity savedEntity = userRepository.save(userEntity);
        log.info("User updated: {}", savedEntity.getEmail());
        return userMapper.toDto(savedEntity);
    }

    /**
     * {@inheritDoc}
     * Требует аутентификации пользователя.
     * Пользователь может изменять только свой собственный пароль.
     * Проверяет корректность текущего пароля перед установкой нового.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updatePassword(NewPasswordDto newPasswordDto) {
        UserEntity userEntity = getCurrentUserEntity();

        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(userEntity.getEmail())) {
            throw new AccessDeniedException("You can only change your own password");
        }

        if (!passwordEncoder.matches(newPasswordDto.getCurrentPassword(), userEntity.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        userEntity.setPassword(passwordEncoder.encode(newPasswordDto.getNewPassword()));
        userRepository.save(userEntity);
        log.info("Password updated for user: {}", userEntity.getEmail());
    }

    /**
     * {@inheritDoc}
     * Требует аутентификации пользователя.
     * Пользователь может обновлять только свой собственный аватар.
     * Удаляет старое изображение перед сохранением нового.
     */
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void updateUserImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image cannot be null or empty");
        }

        UserEntity userEntity = getCurrentUserEntity();

        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(userEntity.getEmail())) {
            throw new AccessDeniedException("You can only update your own avatar");
        }

        if (userEntity.getImage() != null) {
            fileService.deleteImage(userEntity.getImage());
        }

        String filename = fileService.saveImage(image);
        userEntity.setImage(filename);
        userRepository.save(userEntity);
        log.info("User image updated: {}", userEntity.getEmail());
    }
}
