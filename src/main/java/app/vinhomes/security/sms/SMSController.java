package app.vinhomes.security.sms;

import app.vinhomes.security.sms.resource.TwilioHandler;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.ServerResponse;

@RestController
public class SMSController {
    @Autowired
    private TwilioHandler handler;
//    @PostMapping
//    public ServerResponse sentOTP(HttpServletRequest request){
//        return handler.sentOTP(request);
//    }
}
