package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String saveImage(MultipartFile image) throws IOException;

    byte[] getImage(String filename) throws IOException;

    void deleteImage(String filename) throws IOException;

    String updateImage(String oldFilename, MultipartFile newImage) throws IOException;
}
