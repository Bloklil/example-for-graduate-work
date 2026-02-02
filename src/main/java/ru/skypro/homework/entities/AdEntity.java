package ru.skypro.homework.entities;

import lombok.Data;

import javax.persistence.*;

/**
 * Сущность для представления объявлений в базе данных.
 * Соответствует таблице 'ads' и содержит информацию об объявлениях,
 * включая связь с автором (пользователем).
 */
@Entity
@Table(name = "ads")
@Data
public class AdEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private Integer price;

    private String description;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private UserEntity author;
}
