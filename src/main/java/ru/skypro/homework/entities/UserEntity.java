package ru.skypro.homework.entities;

import lombok.Data;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;

/**
 * Сущность для представления пользователей в базе данных.
 * Соответствует таблице 'users' и содержит информацию о пользователях системы,
 * включая учетные данные и личную информацию.
 */
@Entity
@Table(name = "users")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String image;
}
