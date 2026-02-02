package ru.skypro.homework.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;

/**
 * Сущность для представления комментариев в базе данных.
 * Соответствует таблице 'comments' и содержит текст комментария,
 * время создания и связи с пользователем и объявлением.
 */
@Entity
@Table(name = "comments")
@Data
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    private AdEntity ad;
}
