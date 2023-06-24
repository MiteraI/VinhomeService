package app.vinhomes;

import jakarta.annotation.PreDestroy;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})

public class VinhomesApplication {
    public static void main(String[] args) {
        SpringApplication.run(VinhomesApplication.class, args);
    }

}
