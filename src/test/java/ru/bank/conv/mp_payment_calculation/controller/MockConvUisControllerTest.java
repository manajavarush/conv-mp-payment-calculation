package ru.bank.conv.mp_payment_calculation.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.bank.conv.mp_payment_calculation.service.MockConvUisService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MockConvUisControllerTest {

    @Mock
    private MockConvUisService mockService;

    @InjectMocks
    private MockConvUisController controller;

    // Не инициализируем здесь! Поле будет null до @BeforeEach
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // Инициализируем MockMvc ПОСЛЕ того, как Mockito создал controller
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("MockUIS: Должен вернуть 200 OK на каждые первые 4 запроса")
    void shouldReturnOkWhenServiceSuccess() throws Exception {
        // Arrange
        when(mockService.next()).thenReturn(1L);
        when(mockService.isFailure(1L)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/v1/conv-uis-gateway")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"integrationId\":1, \"source\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("MockUIS: Должен вернуть 500 Error на каждый 5-й запрос")
    void shouldReturn500WhenServiceFailure() throws Exception {
        // Arrange
        when(mockService.next()).thenReturn(5L);
        when(mockService.isFailure(5L)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/v1/conv-uis-gateway")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"integrationId\":1, \"source\":\"test\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("UIS is not available"));
    }
}
