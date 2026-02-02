package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.service.FileService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Реализация сервиса для работы с файлами изображений.
 * Сохраняет изображения в директории 'images' в файловой системе.
 * Обеспечивает безопасность доступа к файлам.
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    /** Название директории для хранения изображений */
    private static final String IMAGE_DIR = "images";
    /** Путь к директории изображений */
    private final Path imageDir;

    /**
     * Конструктор создает директорию для изображений, если она не существует.
     *
     * @throws IOException если не удалось создать директорию
     */
    public FileServiceImpl() throws IOException {
        this.imageDir = Paths.get(IMAGE_DIR).toAbsolutePath().normalize();
        Files.createDirectories(this.imageDir);
        log.info("Image directory: {}", this.imageDir);
    }

    /**
     * {@inheritDoc}
     * Генерирует безопасное имя файла на основе временной метки и оригинального имени.
     */
    @Override
    public String saveImage(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Original filename is empty");
        }

        String safeFilename = System.currentTimeMillis() + "_" +
                originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path filePath = this.imageDir.resolve(safeFilename);

        Files.write(filePath, image.getBytes());
        log.info("Image saved: {}", safeFilename);

        return safeFilename;
    }

    /**
     * {@inheritDoc}
     * Выполняет проверку безопасности пути к файлу.
     */
    @Override
    public byte[] getImage(String filename) throws IOException {
        Path filePath = this.imageDir.resolve(filename).normalize();

        if (!filePath.startsWith(this.imageDir)) {
            throw new SecurityException("Access denied to file: " + filename);
        }

        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filename);
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * {@inheritDoc}
     * Выполняет проверку безопасности пути к файлу перед удалением.
     */
    @Override
    public void deleteImage(String filename) throws IOException {
        Path filePath = this.imageDir.resolve(filename).normalize();

        // Проверка безопасности
        if (!filePath.startsWith(this.imageDir)) {
            throw new SecurityException("Access denied to file: " + filename);
        }

        Files.deleteIfExists(filePath);
        log.info("Image deleted: {}", filename);
    }

    /**
     * {@inheritDoc}
     * Комбинирует удаление старого файла и сохранение нового.
     */
    @Override
    public String updateImage(String oldFilename, MultipartFile newImage) throws IOException {
        if (oldFilename != null) {
            deleteImage(oldFilename);
        }
        return saveImage(newImage);
    }
}