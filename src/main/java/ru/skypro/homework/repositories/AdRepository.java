package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.UserEntity;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<AdEntity, Integer> {

    List<AdEntity> findAllByAuthor(UserEntity author);
    List<AdEntity> findAllByAuthorId(Integer authorId);
    List<AdEntity> findAllByTitleContainingIgnoreCase(String title);
}
