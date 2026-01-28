package ru.skypro.homework.mappers;

import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.utils.ImageUrlUtils;

import java.time.Instant;

@Component
public class CommentMapper {
    public CommentDto toDto(CommentEntity entity) {
        if (entity == null) {
            return null;
        }
        CommentDto dto = new CommentDto();
        dto.setPk(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt().toEpochMilli());
        dto.setText(entity.getText());

        UserEntity author = entity.getAuthor();
        if (author != null) {
            dto.setAuthor(author.getId());
            dto.setAuthorFirstName(author.getFirstName());
            dto.setAuthorImage(ImageUrlUtils.createImageUrl(author.getImage()));
        }
        return dto;
    }

    public CommentEntity toEntity(CreateOrUpdateCommentDto dto, UserEntity author) {
        if (dto == null) {
            return null;
        }
        CommentEntity entity = new CommentEntity();
        entity.setText(dto.getText());
        entity.setAuthor(author);
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    public void updateEntityFromDto(CommentEntity entity, CreateOrUpdateCommentDto dto) {
        if (entity == null || dto == null) {
            return;
        }
        if (dto.getText() != null) {
            entity.setText(dto.getText());
        }
    }
}
