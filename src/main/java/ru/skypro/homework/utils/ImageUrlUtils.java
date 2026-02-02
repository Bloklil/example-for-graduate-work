package ru.skypro.homework.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Утилитный класс для работы с URL изображений.
 * Предоставляет методы для генерации URL к изображениям на основе имен файлов.
 */
public class ImageUrlUtils {

    public static String createImageUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        return "/images/" + filename;
    }
}
