package ru.skypro.homework.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ImageUrlUtils {

    public static String createImageUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        return "/images/" + filename;
    }
}
