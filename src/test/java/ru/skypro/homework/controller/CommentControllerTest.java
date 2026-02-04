package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.service.CommentService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CommentControllerTest {
    private static final Long JAN_25_2026 = 1_769_299_200_000_000L;
    private static final Long JAN_26_2026 = 1_769_385_600_000_000L;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CommentService commentService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getComments_shouldReturnCommentsList() throws Exception {

       int adId = 1;

        CommentDto comment1 = new CommentDto();
        comment1.setPk(100);               // id комментария
        comment1.setText("Отличное объявление!");
        comment1.setAuthor(500);           // id автора
        comment1.setAuthorFirstName("Иван");
        comment1.setAuthorImage("/images/user1.jpg");
        comment1.setCreatedAt(JAN_25_2026); // timestamp

        CommentDto comment2 = new CommentDto();
        comment2.setPk(101);
        comment2.setText("Сколько стоит?");
        comment2.setAuthor(501);
        comment2.setAuthorFirstName("Мария");
        comment2.setAuthorImage("/images/user2.jpg");
        comment2.setCreatedAt(JAN_25_2026);

        // 2. Создаем CommentsDto, который вернет сервис
        CommentsDto expectedComments = new CommentsDto();
        expectedComments.setCount(2);
        expectedComments.setResults(List.of(comment1, comment2));

        // 3. Настраиваем мок
        when(commentService.getComments(adId))
                .thenReturn(expectedComments);

        // === ACT & ASSERT ===
        mockMvc.perform(get("/ads/{id}/comments", adId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())  // смотрим что возвращается
                // Базовая проверка статуса
                .andExpect(status().isOk())

                // Проверяем структуру CommentsDto
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(2))

                // Проверяем первый комментарий полностью
                .andExpect(jsonPath("$.results[0].pk").value(100))
                .andExpect(jsonPath("$.results[0].text").value("Отличное объявление!"))
                .andExpect(jsonPath("$.results[0].author").value(500))
                .andExpect(jsonPath("$.results[0].authorFirstName").value("Иван"))
                .andExpect(jsonPath("$.results[0].authorImage").value("/images/user1.jpg"))
                .andExpect(jsonPath("$.results[0].createdAt").value(JAN_25_2026))

                // Проверяем второй комментарий
                .andExpect(jsonPath("$.results[1].pk").value(101))
                .andExpect(jsonPath("$.results[1].text").value("Сколько стоит?"));

        //
        verify(commentService).getComments(adId);
        verifyNoMoreInteractions(commentService);
    }
}
