package com.example.easynotes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();
        server.setUrl("/");
        server.setDescription("Current server");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("EasyNotes API")
                        .description("REST API for a Note Taking Application")
                        .version("1.0.0"));
    }
}
