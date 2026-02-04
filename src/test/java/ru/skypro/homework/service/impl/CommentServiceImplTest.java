package ru.skypro.homework.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.mappers.CollectionMapper;
import ru.skypro.homework.mappers.CommentMapper;
import ru.skypro.homework.repositories.AdRepository;
import ru.skypro.homework.repositories.CommentRepository;
import ru.skypro.homework.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private AdRepository adRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CollectionMapper collectionMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    // Тестовые данные
    private UserEntity testUser;
    private UserEntity testAdmin;
    private UserEntity adAuthor;
    private AdEntity testAd;
    private CommentEntity testComment;
    private CreateOrUpdateCommentDto testCommentDto;

    @BeforeEach
    void setUp() {
        // 1. Создаем обычного пользователя (автор комментария)
        testUser = new UserEntity();
        testUser.setId(1);
        testUser.setEmail("user@example.com");
        testUser.setRole(Role.USER);

        // 2. Создаем администратора
        testAdmin = new UserEntity();
        testAdmin.setId(2);
        testAdmin.setEmail("admin@example.com");
        testAdmin.setRole(Role.ADMIN);

        // 3. Создаем автора объявления
        adAuthor = new UserEntity();
        adAuthor.setId(3);
        adAuthor.setEmail("author@example.com");
        adAuthor.setRole(Role.USER);

        // 4. Создаем тестовое объявление
        testAd = new AdEntity();
        testAd.setId(100);
        testAd.setTitle("Продам машину");
        testAd.setAuthor(adAuthor); // Важно: автор объявления - adAuthor

        // 5. Создаем тестовый комментарий
        testComment = new CommentEntity();
        testComment.setId(500);
        testComment.setText("Отличное объявление!");
        testComment.setAuthor(testUser); // Автор комментария - testUser
        testComment.setAd(testAd);

        // 6. Создаем DTO для создания/обновления комментария
        testCommentDto = new CreateOrUpdateCommentDto();
        testCommentDto.setText("Новый комментарий");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void setupCurrentUser(String email) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getComments_whenAdExists_shouldReturnComments() {
        // 1. ПОДГОТОВКА: Настраиваем моки
        when(adRepository.findById(100)).thenReturn(Optional.of(testAd));
        when(commentRepository.findAllByAd(testAd)).thenReturn(List.of(testComment));

        CommentsDto expectedDto = new CommentsDto();
        expectedDto.setCount(1);
        when(collectionMapper.commentsToDto(anyList())).thenReturn(expectedDto);

        // 2. ВЫПОЛНЕНИЕ: Вызываем метод
        CommentsDto result = commentService.getComments(100);

        // 3. ПРОВЕРКА: Проверяем результат
        assertNotNull(result);
        assertEquals(1, result.getCount());
    }

    @Test
    void createComment_shouldCreateAndReturnComment() {
        // 1. ПОДГОТОВКА
        setupCurrentUser("user@example.com"); // Имитируем пользователя

        when(adRepository.findById(100)).thenReturn(Optional.of(testAd));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));

        // Мокируем создание и сохранение комментария
        CommentEntity newComment = new CommentEntity();
        when(commentMapper.toEntity(any(), any())).thenReturn(newComment);
        when(commentRepository.save(newComment)).thenReturn(newComment);

        CommentDto expectedDto = new CommentDto();
        when(commentMapper.toDto(newComment)).thenReturn(expectedDto);

        // 2. ВЫПОЛНЕНИЕ
        CommentDto result = commentService.createComment(100, testCommentDto);

        // 3. ПРОВЕРКА
        assertNotNull(result);
        // Важно: комментарий должен быть привязан к объявлению
        assertEquals(testAd, newComment.getAd());
    }

    @Test
    void updateComment_whenUserIsCommentAuthor_shouldUpdate() {
        // 1. ПОДГОТОВКА: Автор комментария пытается обновить
        setupCurrentUser("user@example.com");

        when(adRepository.findById(100)).thenReturn(Optional.of(testAd));
        when(commentRepository.findByIdAndAd(500, testAd)).thenReturn(Optional.of(testComment));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));

        // 2. ВЫПОЛНЕНИЕ
        commentService.updateComment(100, 500, testCommentDto);

        // 3. ПРОВЕРКА
        // Автор может обновить свой комментарий
        verify(commentMapper).updateEntityFromDto(testComment, testCommentDto);
        verify(commentRepository).save(testComment);
    }

    @Test
    void updateComment_whenUserIsAdmin_shouldUpdate() {
        // 1. ПОДГОТОВКА: Админ пытается обновить чужой комментарий
        setupCurrentUser("admin@example.com");

        when(adRepository.findById(100)).thenReturn(Optional.of(testAd));
        when(commentRepository.findByIdAndAd(500, testAd)).thenReturn(Optional.of(testComment));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(testAdmin));

        // 2. ВЫПОЛНЕНИЕ
        commentService.updateComment(100, 500, testCommentDto);

        // 3. ПРОВЕРКА
        // Админ может обновить ЛЮБОЙ комментарий
        verify(commentMapper).updateEntityFromDto(testComment, testCommentDto);
    }

    @Test
    void updateComment_whenUserHasNoRights_shouldThrowException() {
        // 1. ПОДГОТОВКА: Чужой пользователь пытается обновить
        UserEntity otherUser = new UserEntity();
        otherUser.setId(999);
        otherUser.setEmail("other@example.com");
        otherUser.setRole(Role.USER);

        setupCurrentUser("other@example.com");

        when(adRepository.findById(100)).thenReturn(Optional.of(testAd));
        when(commentRepository.findByIdAndAd(500, testAd)).thenReturn(Optional.of(testComment));
        when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

        // 2. ВЫПОЛНЕНИЕ & 3. ПРОВЕРКА
        // Чужой пользователь НЕ может обновить комментарий
        assertThrows(AccessDeniedException.class, () -> {
            commentService.updateComment(100, 500, testCommentDto);
        });
    }

    @Test
    void deleteComment_byCommentAuthor_shouldDelete() {
        setupCurrentUser("user@example.com");

        when(adRepository.findById(100)).thenReturn(Optional.of(testAd));
        when(commentRepository.findByIdAndAd(500, testAd)).thenReturn(Optional.of(testComment));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));

        commentService.deleteComment(100, 500);

        verify(commentRepository).delete(testComment);
    }

    @Test
    void deleteComment_byAdmin_shouldDelete() {
        setupCurrentUser("admin@example.com");

        when(adRepository.findById(100)).thenReturn(Optional.of(testAd));
        when(commentRepository.findByIdAndAd(500, testAd)).thenReturn(Optional.of(testComment));
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(testAdmin));

        commentService.deleteComment(100, 500);

        verify(commentRepository).delete(testComment);
    }
}