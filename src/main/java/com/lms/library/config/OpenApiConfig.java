package com.lms.library.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * Configures API documentation settings and schema mappings.
 *
 * @author Joel Silva
 * @version 1.0
 * @since 2025
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Library Management System API",
                version = "1.0.0",
                description = "REST API for managing library operations including book checkout, returns, and member management",
                contact = @Contact(
                        name = "Library Support",
                        email = "joeltadeu@gmail.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Development Server"
                )
        }
)
@Configuration
public class OpenApiConfig {

    /**
     * Default constructor for OpenApiConfig.
     */
    public OpenApiConfig() {
    }

    static {
        SpringDocUtils.getConfig().replaceWithSchema(LocalDate.class,
                new Schema<LocalDate>()
                        .type("string")
                        .format("date")
                        .example(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
        );
    }
}
