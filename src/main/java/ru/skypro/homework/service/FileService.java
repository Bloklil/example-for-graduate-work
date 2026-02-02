package ru.skypro.homework.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Сервис для работы с файлами изображений.
 * Предоставляет операции для сохранения, получения и удаления изображений.
 */
public interface FileService {

    /**
     * Сохраняет изображение в файловой системе.
     *
     * @param image файл изображения для сохранения
     * @return имя сохраненного файла
     * @throws IOException если произошла ошибка при записи файла
     * @throws IllegalArgumentException если имя файла пустое или null
     */
    String saveImage(MultipartFile image) throws IOException;

    /**
     * Получает изображение по имени файла.
     *
     * @param filename имя файла изображения
     * @return массив байтов изображения
     * @throws IOException если файл не найден или произошла ошибка при чтении
     * @throws SecurityException если попытка доступа к файлу за пределами разрешенной директории
     */
    byte[] getImage(String filename) throws IOException;

    /**
     * Удаляет изображение по имени файла.
     *
     * @param filename имя файла изображения для удаления
     * @throws IOException если произошла ошибка при удалении файла
     * @throws SecurityException если попытка доступа к файлу за пределами разрешенной директории
     */
    void deleteImage(String filename) throws IOException;

    /**
     * Обновляет изображение, удаляя старое и сохраняя новое.
     *
     * @param oldFilename имя старого файла (может быть null)
     * @param newImage новый файл изображения
     * @return имя нового сохраненного файла
     * @throws IOException если произошла ошибка при работе с файлами
     */
    String updateImage(String oldFilename, MultipartFile newImage) throws IOException;
}
