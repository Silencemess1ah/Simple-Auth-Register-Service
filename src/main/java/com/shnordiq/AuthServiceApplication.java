package com.shnordiq;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Auth Service for ShnordiQ",
        description = "Auth Service", version = "1.0.0",
        contact = @Contact(
                name = "Sergey Sklyar aka mad_owl91",
                email = "sergeysklyar1091@gmail.com",
                url = "https://github.com/Silencemess1ah")))
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

}
