package app.vinhomes.security.sms.resource;

import app.vinhomes.security.sms.dto.OTPAttribute;
import app.vinhomes.security.sms.dto.OTPStatus;
import app.vinhomes.security.sms.dto.PasswordResetDTO;
import app.vinhomes.security.sms.dto.PasswordResetResponseDTO;
import app.vinhomes.security.sms.service.TwilioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class TwilioHandler {
    @Autowired
    private TwilioService twilioService;
    private String phoneParameterName= "phoneNumber";
    private String OTPParameterName = "oneTimePassword";
    public ServerResponse sentOTP(ServerRequest serverRequest)  {
        System.out.println("in sent OTP");
        boolean checkIfParameterExist = serverRequest.param(phoneParameterName).isEmpty();
        String phonenumber = checkIfParameterExist ? "" : serverRequest.param(phoneParameterName).get();
        return ServerResponse.ok().body(
                 twilioService.OTPPasswordReset(phonenumber)
       );
    }


    public ServerResponse validateOTP(ServerRequest serverRequest)  {
        boolean checkPhoneParamExist = serverRequest.param(phoneParameterName).isEmpty();
        String phonenumber = checkPhoneParamExist ? "" : serverRequest.param(phoneParameterName).get();
        boolean checkOtpParamExist = serverRequest.param(OTPParameterName).isEmpty();
        String OTP = checkOtpParamExist ?   "" : serverRequest.param(OTPParameterName).get();
        System.out.println("in validate OTP");
        return ServerResponse.ok().body(
                twilioService.validateOTP(OTP,phonenumber)
        );
    }

    public ServerResponse getOTPmap(ServerRequest serverRequest){
        System.out.println("inside getOTPmap");
        Map<String, OTPAttribute> list = twilioService.getOTPMap();
        return ServerResponse.ok().body(list);
    }

}
