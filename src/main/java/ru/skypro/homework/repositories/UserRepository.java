package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.UserEntity;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями пользователей в базе данных.
 * Расширяет JpaRepository и предоставляет методы для поиска пользователей по email.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
