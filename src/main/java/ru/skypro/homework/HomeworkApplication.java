package ru.skypro.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Основной класс Spring Boot приложения.
 * Содержит точку входа (main метод) для запуска приложения.
 * Настраивает автоконфигурацию Spring Boot и сканирование компонентов.
 */
@SpringBootApplication
public class HomeworkApplication {
  public static void main(String[] args) {
    SpringApplication.run(HomeworkApplication.class, args);
  }
}
