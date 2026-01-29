package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceImplTest {

    private FileServiceImpl fileService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileService = new FileServiceImpl();
    }

    @AfterEach
    void tearDown() throws IOException {
        // Очищаем папку images после каждого теста
        Path imagesDir = Paths.get("images");
        if (Files.exists(imagesDir)) {
            Files.walk(imagesDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @Test
    void saveImage_shouldSaveFileAndReturnFilename() throws IOException {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "cat.jpg",
                "image/jpeg",
                "fake image data".getBytes()
        );

        String filename = fileService.saveImage(image);

        assertNotNull(filename, "Имя файла не должно быть null");
        assertTrue(filename.contains("cat.jpg"),
                "Имя файла должно содержать оригинальное имя");
        assertTrue(filename.matches("\\d+_cat\\.jpg"),
                "Имя файла должно быть в формате: timestamp_name.jpg");

        // Файл действительно создался
        Path expectedPath = Paths.get("images", filename);
        assertTrue(Files.exists(expectedPath));

        // Содержимое файла совпадает
        byte[] savedContent = Files.readAllBytes(expectedPath);
        assertArrayEquals("fake image data".getBytes(), savedContent);
    }

    @Test
    void getImage_whenFileExists_shouldReturnBytes() throws IOException {
        String testFilename = "test_image.jpg";
        Path testFilePath = Paths.get("images", testFilename);

        // Создаем директорию
        Files.createDirectories(Paths.get("images"));

        // Записываем тестовые данные
        byte[] expectedData = "test image content".getBytes();
        Files.write(testFilePath, expectedData);

        // Читаем файл через сервис
        byte[] result = fileService.getImage(testFilename);

        // Полученные данные должны совпадать с записанными
        assertArrayEquals(expectedData, result);

    }

    @Test
    void getImage_whenFileNotExists_shouldThrowException() {
        // Проверяем, что выбрасывается исключение при отсутствии файла
        assertThrows(IOException.class, () -> {
            fileService.getImage("non_existing_file.jpg");
        });
    }

    @Test
    void deleteImage_shouldDeleteFileIfExists() throws IOException {
        String testFilename = "to_delete.jpg";
        Path testFilePath = Paths.get("images", testFilename);

        Files.createDirectories(Paths.get("images"));
        Files.write(testFilePath, "data".getBytes());

        // Убеждаемся, что файл существует
        assertTrue(Files.exists(testFilePath));

        // Удаляем через сервис
        fileService.deleteImage(testFilename);

        // Файл больше не существует
        assertFalse(Files.exists(testFilePath));
    }

    @Test
    void updateImage_shouldReplaceOldFileWithNew() throws IOException {

        String oldFilename = "old_image.jpg";
        Path oldFilePath = Paths.get("images", oldFilename);

        Files.createDirectories(Paths.get("images"));
        Files.write(oldFilePath, "old content".getBytes());

        // Создаем новую картинку
        MockMultipartFile newImage = new MockMultipartFile(
                "image",
                "new_cat.jpg",
                "image/jpeg",
                "new content".getBytes()
        );

        // Обновляем картинку
        String newFilename = fileService.updateImage(oldFilename, newImage);

        // Старый файл удален
        assertFalse(Files.exists(oldFilePath));

        // Новый файл создан с правильным именем
        Path newFilePath = Paths.get("images", newFilename);
        assertTrue(Files.exists(newFilePath));
        assertTrue(newFilename.contains("new_cat.jpg"));

        // Содержимое нового файла правильное
        byte[] newContent = Files.readAllBytes(newFilePath);
        assertArrayEquals("new content".getBytes(), newContent);

        // Удаляем файл
        Files.deleteIfExists(newFilePath);
    }
}