package app.vinhomes.security.sms.resource;

import org.apache.catalina.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class TwilioRouterConfig {
    @Autowired
    private TwilioHandler handler;
    @Bean
    public RouterFunction<ServerResponse> handleSMS(){
        return RouterFunctions.route()
                .POST("/router/sendOTP",handler::sentOTP)
                .POST("/router/validateOTP",handler::validateOTP)
                .GET("/router/getOTPmap",handler::getOTPmap)
                .build();
    }
}
