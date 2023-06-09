package app.vinhomes.security.sms.service;

import app.vinhomes.entity.customer.Phone;
import app.vinhomes.security.sms.config.TwilioConfig;
import app.vinhomes.security.sms.dto.OTPAttribute;
import app.vinhomes.security.sms.dto.OTPStatus;
import app.vinhomes.security.sms.dto.PasswordResetDTO;
import app.vinhomes.security.sms.dto.PasswordResetResponseDTO;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.fasterxml.jackson.databind.type.LogicalType.DateTime;

@Service
public class TwilioService {
    private String OTPpattern = "000000";
    private int integerBound = 999999;
    private Map<String, OTPAttribute> OTPMap = new HashMap<>();
//    @Value("${twilio.expiration}")
    private long expriredTimeSecond = 1000*60 ;
    @Autowired
    private TwilioConfig twilioConfig;
//    PasswordResetDTO passwordResetDTO
    public PasswordResetResponseDTO OTPPasswordReset(String incomingPhonenumber) {
        try {
            String phonenumber = parsePhoneNumberToCorrectZone(incomingPhonenumber);
            PhoneNumber to = new PhoneNumber(phonenumber);
            PhoneNumber from = new PhoneNumber(twilioConfig.getTrialnumber());
            boolean checkIfPhonenumberExist = checkIfMoreThanOneOrExistOneOTP(phonenumber);
            if(checkIfPhonenumberExist){
                OTPMap.remove(phonenumber);
            }
            String otp = generateOTP();
            String messageBody = "this is a message from spring: "
                    + otp + " this is the otp generator";
            System.out.println(messageBody);
            Message message = Message.creator(to,from,messageBody).create();
            OTPAttribute otpAttribute = new OTPAttribute(otp, getExpired());
            OTPMap.put(phonenumber,otpAttribute);
            return  new PasswordResetResponseDTO(OTPStatus.DELIVERED,messageBody);
        }catch (Exception e){
            return  new PasswordResetResponseDTO(OTPStatus.FAIL,e.getMessage());
        }
    }

    public String validateOTP(String incomingOTP,String incomingPhonenumber){
        try{
        String phonenumber = parsePhoneNumberToCorrectZone(incomingPhonenumber);
        if(incomingOTP.equals(OTPMap.get(phonenumber).getOneTimePassword())){
            System.out.println("time set expired" + OTPMap.get(phonenumber).getExpired());
            System.out.println("time now" + new Date());
            if(isOTPExpired(OTPMap.get(phonenumber).getExpired())){
                OTPMap.remove(phonenumber);
                return "OTP expired, send get another one ";
            }
            OTPMap.remove(phonenumber);
            return "OTP matches, can do transaction or get password or stuff";
        }
        else{
            return "ERROR, invalid OTP, try choose resent OTP";
        }
        }catch (Exception e){
            System.out.println("something wrong with phone, maybe not correct return, ");
            return e.getMessage();
        }

    }

    private String generateOTP(){// 6 digit
        return new DecimalFormat(OTPpattern)
                .format(new Random().nextInt(integerBound));

    }
    // return false is good in this case, mean no dulplicate token
    private boolean checkIfMoreThanOneOrExistOneOTP(String phonenumber){
        try{
            OTPAttribute attribute = OTPMap.get(phonenumber);
            if(attribute != null){
                System.out.println("there are already an exist token, proceed to delete it to make new one");
                return true;
            }else{
                System.out.println("yes safe, no other token for this user");
                return false;
            }
        }catch (Exception e){
            System.out.println("Error finding OTP in map");
            return true;
        }
    }
    private Date getExpired(){
        return new Date(System.currentTimeMillis()+ expriredTimeSecond) ;
    }
    private String parsePhoneNumberToCorrectZone(String phonenumber){// change to VIETNAM zone +84
        if(phonenumber.startsWith("+84") == false){
            StringBuilder VNPhoneNumber = new StringBuilder("+84" + phonenumber.substring(1));
            return VNPhoneNumber.toString();
        }else{
            return phonenumber;
        }
    }
    private boolean isOTPExpired(Date expired){
        return expired.before(new Date());
    }
    public Map<String, OTPAttribute> getOTPMap(){
        return this.OTPMap;
    }

}
