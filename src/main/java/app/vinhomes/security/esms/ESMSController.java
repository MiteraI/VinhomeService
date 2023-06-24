package app.vinhomes.security.esms;

import app.vinhomes.security.esms.otp_dto.EnumOTPStatus;
import app.vinhomes.security.esms.otp_dto.OTPResponseStatus;
import app.vinhomes.security.esms.otp_service.ESMSservice;
import app.vinhomes.security.esms.otp_service.OTPService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/esms")
public class ESMSController {
    private String phoneParameterName= "phoneNumber";
    private String OTPParameterName = "oneTimePassword";
    private String MessageParameterName = "message";
    @Autowired
    private ESMSservice service;
    @Autowired
    private OTPService otpService;
    @PostMapping(value = "/sendnormal")
    public ResponseEntity<?> sendESMS(HttpServletRequest request) throws IOException {
        System.out.println("in send normal sms");
        String phonenumber = request.getParameter(phoneParameterName);
        String message = request.getParameter(MessageParameterName);
        String result = service.sendGetJSON(phonenumber,message);
        return ResponseEntity.ok().body(result);
    }
    @PostMapping(value = "/sendotp")
    public ResponseEntity<?> sendOTP(HttpServletRequest request) throws IOException {
        System.out.println("in sent OTP");
        boolean checkIfParameterExist = request.getParameter(phoneParameterName).isEmpty();
        String phonenumber = checkIfParameterExist ? "" : request.getParameter(phoneParameterName);
        if(phonenumber.length() != 10){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid phonenumber");
        }
        OTPResponseStatus OTP = otpService.generateOTPCode_Message(phonenumber);
        if(OTP.getStatus().equals(EnumOTPStatus.CREATED)){
            return ResponseEntity.ok().body(service.sendGetJSON(phonenumber,OTP.getMessage()));
        }else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something wrong when cooking OTP");
        }
    }
    @PostMapping(value = "/validateOTP")
    public ResponseEntity<?> validateOTP(HttpServletRequest request) {
        try{
            System.out.println("in validate OTP");
            boolean checkPhoneParamExist = request.getParameter(phoneParameterName).isEmpty();
            String phonenumber = checkPhoneParamExist ? "" : request.getParameter(phoneParameterName);
            String OTP =  request.getParameter(OTPParameterName);
            if(phonenumber.length() != 10){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("WHY THERE ARE NO PHONE NUMBER IN THIS, IT SUPPOSE TO SEND BACK WITH PHONE NUMBER INCLUDED ");
            }
            return ResponseEntity.ok().body(otpService.validateOTP(OTP,phonenumber));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("asdfdsfa");
        }

    }
}
