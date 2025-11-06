package com.lms.library.karate;

import com.lms.library.Application;
import com.intuit.karate.Results;
import com.intuit.karate.junit5.Karate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {Application.class})

public class BorrowAndReturnBooksTest {
    @Autowired
    Environment environment;

    @Karate.Test
    public Karate test() {
        Karate karate =
                Karate.run("classpath:karate/BorrowAndReturnBooks.feature")
                        .outputCucumberJson(true)
                        .systemProperty("port", environment.getProperty("local.server.port"));

        Results results = karate.parallel(1);

        assertTrue(results.getErrorMessages(), results.getFailCount() == 0);
        return karate;
    }
}
