package app.vinhomes;

import app.vinhomes.security.sms.config.TwilioConfig;
import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class VinhomesApplication {
    @Autowired
    private TwilioConfig twilioConfig;
    @PostConstruct
    public void intiTwilio(){
        Twilio.init(twilioConfig.getAccoundsid(),twilioConfig.getAuthtoken());
    }
    public static void main(String[] args) {
        SpringApplication.run(VinhomesApplication.class, args);
    }

}
