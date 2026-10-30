package com.asmaeel.tazkara.booking;

import com.asmaeel.tazkara.shared.config.OpenApiConfig;
import com.asmaeel.tazkara.shared.exception.GlobalExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({GlobalExceptionHandler.class, OpenApiConfig.class})
@SpringBootApplication
public class BookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingServiceApplication.class, args);
    }

}
