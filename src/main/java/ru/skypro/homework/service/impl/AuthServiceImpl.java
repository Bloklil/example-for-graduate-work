package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.mappers.UserMapper;
import ru.skypro.homework.repositories.UserRepository;
import ru.skypro.homework.service.AuthService;

/**
 * Реализация сервиса аутентификации и регистрации.
 * Обрабатывает логику входа и регистрации пользователей.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    /**
     * {@inheritDoc}
     *
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException если пользователь не найден
     */
    @Override
    public boolean login(String userName, String password) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            return passwordEncoder.matches(password, userDetails.getPassword());
        } catch (Exception e) {
            log.error("Login failed for user: {}", userName, e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException если данные регистрации некорректны
     */
    @Override
    public boolean register(Register register) {
        if (userRepository.existsByEmail(register.getUsername())) {
            log.warn("Registration failed: user {} already exists", register.getUsername());
            return false;
        }

        try {
            UserEntity userEntity = userMapper.toEntity(register);
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

            userRepository.save(userEntity);
            log.info("User registered successfully: {}", register.getUsername());
            return true;
        } catch (Exception e) {
            log.error("Registration failed for user: {}", register.getUsername(), e);
            return false;
        }
    }

}
