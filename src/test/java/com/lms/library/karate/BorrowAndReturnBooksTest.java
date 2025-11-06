package com.lms.library.karate;

import static org.springframework.test.util.AssertionErrors.assertTrue;

import com.intuit.karate.junit5.Karate;
import com.lms.library.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {Application.class})
public class BorrowAndReturnBooksTest {
  @Autowired Environment environment;

  @Karate.Test
  public Karate test() {
    var karate =
        Karate.run("classpath:karate/BorrowAndReturnBooks.feature")
            .outputCucumberJson(true)
            .systemProperty("port", environment.getProperty("local.server.port"));

    var results = karate.parallel(1);

    assertTrue(results.getErrorMessages(), results.getFailCount() == 0);
    return karate;
  }
}
