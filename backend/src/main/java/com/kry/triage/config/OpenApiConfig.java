package com.kry.triage.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI triageOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medical Triage API")
                        .description("API for triage assessment and appointment booking")
                        .version("v1")
                        .contact(new Contact().name("KRY Assignment"))
                        .license(new License().name("Internal Use")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project README")
                        .url("/"));
    }
}
