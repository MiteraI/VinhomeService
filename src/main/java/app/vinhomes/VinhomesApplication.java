package app.vinhomes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
@ServletComponentScan
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class VinhomesApplication {

    public static void main(String[] args) {
        SpringApplication.run(VinhomesApplication.class, args);
    }


}
