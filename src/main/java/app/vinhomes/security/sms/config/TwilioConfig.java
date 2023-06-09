package app.vinhomes.security.sms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
//@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioConfig {
    @Value("${twilio.account_sid}")
    private String accoundsid;
    @Value("${twilio.auth_token}")
    private String authtoken;
    @Value("${twilio.trial_number}")
    private String trialnumber;
}
