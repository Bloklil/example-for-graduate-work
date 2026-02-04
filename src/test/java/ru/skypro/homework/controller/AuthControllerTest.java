package ru.skypro.homework.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.repositories.UserRepository;
import ru.skypro.homework.service.AuthService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;
    @MockBean
    private UserRepository userRepository;

    @Test
    void loginSuccessTest() throws Exception {
        Login login = new Login();
        login.setUsername("test@example.com");
        login.setPassword("123");

        UserEntity fakeUser = new UserEntity();
        fakeUser.setId(1);
        fakeUser.setEmail("test@example.com");

        when(authService.login(login.getUsername(), login.getPassword()))
                .thenReturn(true);
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(fakeUser));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").doesNotExist());

        verify(authService).login(login.getUsername(), login.getPassword());
        verify(userRepository).findByEmail("test@example.com");

    }

    @Test
    void loginFailTest() throws Exception {
        Login login = new Login();
        login.setUsername("test@example.com");
        login.setPassword("wrong_password");

        when(authService.login("test@example.com", "wrong_password")).thenReturn(false);

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").doesNotExist());

        verify(authService).login("test@example.com", "wrong_password");
        verify(userRepository, never()).findByEmail(anyString());

    }

}
