package com.lms.library;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class ApplicationTest {

    @Test
    void shouldRunSpringApplication() {
        try (MockedStatic<SpringApplication> mockSpringApp = mockStatic(SpringApplication.class)) {
            mockSpringApp.when(() -> SpringApplication.run(Application.class))
                    .thenReturn(null);

            new Application().main();

            mockSpringApp.verify(() -> SpringApplication.run(Application.class));
        }
    }

    @Test
    void shouldNotThrowWhenStartingApp() {
        assertDoesNotThrow(() -> new Application().main());
    }
}
