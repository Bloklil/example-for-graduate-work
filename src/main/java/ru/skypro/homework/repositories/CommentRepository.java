package ru.skypro.homework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {

    List<CommentEntity> findAllByAd(AdEntity ad);
    List<CommentEntity> findAllByAdId(Integer adId);
    void deleteAllByAd(AdEntity ad);
    Optional<CommentEntity> findByIdAndAd(Integer id, AdEntity ad);
}
