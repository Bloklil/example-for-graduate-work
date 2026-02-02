package ru.skypro.homework.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.utils.ImageUrlUtils;

@Component
@RequiredArgsConstructor
public class AdMapper {
    public AdDto toDto(AdEntity entity) {
        if (entity == null) {
            return null;
        }
        AdDto dto = new AdDto();
        dto.setPk(entity.getId());
        dto.setAuthor(entity.getAuthor().getId());
        dto.setImage(ImageUrlUtils.createImageUrl(entity.getImage()));
        dto.setPrice(entity.getPrice());
        dto.setTitle(entity.getTitle());

        if (entity.getImage() != null && !entity.getImage().isEmpty()) {
            dto.setImage("/images/" + entity.getImage());
        } else {
            dto.setImage(null);
        }

        return dto;
    }

    public ExtendedAdDto toExtendedDto(AdEntity entity) {
        if (entity == null) {
            return null;
        }
        ExtendedAdDto dto = new ExtendedAdDto();
        dto.setPk(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setPrice(entity.getPrice());
        dto.setDescription(entity.getDescription());
        dto.setImage(ImageUrlUtils.createImageUrl(entity.getImage()));

        UserEntity author = entity.getAuthor();
        if (author != null) {
            dto.setAuthorFirstName(author.getFirstName());
            dto.setAuthorLastName(author.getLastName());
            dto.setEmail(author.getEmail());
            dto.setPhone(author.getPhone());
        }
        return dto;
    }

    public AdEntity toEntity(CreateOrUpdateAdDto dto, UserEntity author) {
        if (dto == null) {
            return null;
        }
        AdEntity entity = new AdEntity();
        entity.setTitle(dto.getTitle());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        entity.setAuthor(author);
        return entity;
    }

    public void updateEntityFromDto(AdEntity entity, CreateOrUpdateAdDto dto) {
        if (entity == null || dto == null) {
            return;
        }
        if (dto.getTitle() != null) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getPrice() != null) {
            entity.setPrice(dto.getPrice());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
    }
}
