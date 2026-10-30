package com.asmaeel.tazkara.identity;

import com.asmaeel.tazkara.shared.config.OpenApiConfig;
import com.asmaeel.tazkara.shared.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@Import({GlobalExceptionHandler.class, OpenApiConfig.class})
@SpringBootApplication
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }

}
