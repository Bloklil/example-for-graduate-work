package ru.skypro.homework.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class ImageUrlUtils {

    public static String createImageUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }

        try {
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/images/")
                    .path(filename)
                    .toUriString();
        } catch (Exception e) {
            return "/images/" + filename;
        }
    }
}
