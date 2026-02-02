package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.service.AdService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AdService adService;

    @Test
    void getAllAds() throws Exception {
        AdsDto ads = new AdsDto();
        when(adService.getAllAds()).thenReturn(ads);

        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk());
    }

    @Test
    void createAd() throws Exception {
        // Создаем фейковый файл
        MockMultipartFile image = new MockMultipartFile(
                "image",           // Имя поля в форме
                "test.jpg",        // Имя файла
                "image/jpeg",      // MIME тип
                "test".getBytes()  // Содержимое
        );

        mockMvc.perform(multipart("/ads")
                        .file(image)
                        .param("title", "iPhone")
                        .param("price", "50000")
                        .param("description", "Хороший телефон"))
                .andExpect(status().isCreated());
    }

    @Test
    void getAdByIdTest() throws Exception {
        // 1. Arrange - мок для adService.getAdById(id)
        ExtendedAdDto fakeAd = new ExtendedAdDto();
        fakeAd.setPk(1);
        fakeAd.setTitle("iPhone");
        when(adService.getAdById(1)).thenReturn(fakeAd);

        // 2. Act - GET /ads/1
        mockMvc.perform(get("/ads/1"))
                // 3. Assert - статус 200
                .andExpect(status().isOk());
    }

    @Test
    void updateAdTest() throws Exception {
        // Arrange
        AdDto fakeAd = new AdDto();
        fakeAd.setPk(1);
        fakeAd.setTitle("iPhone");
        fakeAd.setPrice(50000);

        CreateOrUpdateAdDto fakeDto = new CreateOrUpdateAdDto();
        fakeDto.setPrice(40000);
        when(adService.updateAd(1, fakeDto)).thenReturn(fakeAd);

        // Act
        mockMvc.perform(patch("/ads/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fakeDto)))

                // Assert
                .andExpect(status().isOk());
    }

    @Test
    void deleteAdTest() throws Exception {
        mockMvc.perform(delete("/ads/1"))
                .andExpect(status().isNoContent());
        verify(adService).deleteAd(1);
    }

    @Test
    void updateImageTest() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",           // ← точно как @RequestPart("image")
                "test.jpg",
                "image/jpeg",
                "test content".getBytes());

        doNothing().when(adService).updateAdImage(1, image);

        mockMvc.perform(multipart(HttpMethod.PATCH, "/ads/1/image")
                        .file(image))
                .andDo(print())
                .andExpect(status().isOk());

        verify(adService).updateAdImage(1, image);
    }


}