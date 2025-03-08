package ru.practicum.shareit.server.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({ErrorHandler.class, ErrorHandlerTest.TestController.class}) // Тестовый контроллер
class ErrorHandlerTest {

    @org.springframework.beans.factory.annotation.Autowired
    private MockMvc mockMvc;

    @MockBean
    private TestController testController;

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping
        public void testMethod() {

        }
    }

    @Test
    void shouldReturnBadRequestOnIllegalArgumentException() throws Exception {
        doThrow(new IllegalArgumentException("Invalid state"))
                .when(testController).testMethod();

        mockMvc.perform(get("/test"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid state"));
    }

    @Test
    void shouldReturnForbiddenOnForbiddenException() throws Exception {
        doThrow(new ForbiddenException("Access denied"))
                .when(testController).testMethod();

        mockMvc.perform(get("/test"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    void shouldReturnConflictOnIllegalStateException() throws Exception {
        doThrow(new IllegalStateException("Email already in use"))
                .when(testController).testMethod();

        mockMvc.perform(get("/test"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already in use"));
    }

    @Test
    void shouldReturnNotFoundOnNoSuchElementException() throws Exception {
        doThrow(new NoSuchElementException("User not found"))
                .when(testController).testMethod();

        mockMvc.perform(get("/test"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void shouldReturnInternalServerErrorOnGenericException() throws Exception {
        doThrow(new RuntimeException("Unexpected error"))
                .when(testController).testMethod();

        mockMvc.perform(get("/test"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", containsString("Unexpected error")));
    }
}