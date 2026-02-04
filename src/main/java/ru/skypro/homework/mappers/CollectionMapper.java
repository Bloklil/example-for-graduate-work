package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования коллекций сущностей в соответствующие DTO.
 * Специализируется на преобразовании списков объявлений и комментариев.
 */
@Component
public class CollectionMapper {
    private final AdMapper adMapper;
    private final CommentMapper commentMapper;

    public CollectionMapper(AdMapper adMapper, CommentMapper commentMapper) {
        this.adMapper = adMapper;
        this.commentMapper = commentMapper;
    }

    public AdsDto adsToDto(List<AdEntity> ads) {
        AdsDto dto = new AdsDto();
        dto.setCount(ads.size());
        dto.setResults(ads.stream()
                .map(adMapper::toDto)
                .collect(Collectors.toList()));
        return dto;
    }

    public CommentsDto commentsToDto(List<CommentEntity> comments) {
        CommentsDto dto = new CommentsDto();
        dto.setCount(comments.size());
        dto.setResults(comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList()));
        return dto;
    }
}
