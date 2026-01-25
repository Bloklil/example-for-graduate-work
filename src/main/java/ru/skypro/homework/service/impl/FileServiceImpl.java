package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final String IMAGE_DIR = "images";

    @Override
    public String saveImage(MultipartFile image) throws IOException {
        Files.createDirectories(Paths.get(IMAGE_DIR));

        String filename = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path filePath = Paths.get(IMAGE_DIR, filename);

        Files.write(filePath, image.getBytes());
        log.info("Image saved: {}", filename);

        return filename;
    }

    @Override
    public byte[] getImage(String filename) throws IOException {
        Path path = Paths.get(IMAGE_DIR, filename);
        return Files.readAllBytes(path);
    }

    @Override
    public void deleteImage(String filename) throws IOException {
        Path path = Paths.get(IMAGE_DIR, filename);
        Files.deleteIfExists(path);
        log.info("Image deleted: {}", filename);
    }

    @Override
    public String updateImage(String oldFilename, MultipartFile newImage) throws IOException {
        deleteImage(oldFilename);
        return saveImage(newImage);
    }
}