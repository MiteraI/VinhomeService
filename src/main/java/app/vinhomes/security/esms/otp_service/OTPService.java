package app.vinhomes.security.esms.otp_service;

import app.vinhomes.security.esms.otp_dto.OTPAttribute;
import app.vinhomes.security.esms.otp_dto.EnumOTPStatus;
import app.vinhomes.security.esms.otp_dto.OTPResponseStatus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
//@SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE")
public class OTPService {
    @Value("${time.otp_timeout}")
    private int OTP_TIMEOUT_SECOND;
    private final String OTPpattern = "000000";
    private final int integerBound = 999999;
    private final Map<String, OTPAttribute> OTPMap = new HashMap<>();
    //private final long expriredTimeSecond = 1000 * OTP_TIMEOUT_SECOND ;
    public OTPResponseStatus generateOTPCode_Message(String phonenumber) {
        try {
            boolean checkIfPhonenumberExist = checkIfMoreThanOneOrExistOneOTP(phonenumber);
            if(checkIfPhonenumberExist){
                OTPMap.remove(phonenumber);
            }
            String OTP = generateOTP();
            String messageBody = "this is a message from spring boot, your OTP" +
                    "is:   "+ OTP;
            System.out.println(messageBody);
            OTPAttribute otpAttribute = new OTPAttribute(OTP,getExpiredTime());
            OTPMap.put(phonenumber,otpAttribute);
            return  new OTPResponseStatus(EnumOTPStatus.CREATED,messageBody);
        }catch (Exception e){
            return  new OTPResponseStatus(EnumOTPStatus.FAIL,e.getMessage());
        }
    }

    public String validateOTP(String incomingOTP,String phonenumber) {
        try {
            if (incomingOTP.equals(OTPMap.get(phonenumber).getOneTimePassword())) {
                System.out.println("time set expired " + OTPMap.get(phonenumber).getExpired());
                System.out.println("time now         " + LocalDateTime.now());
                if (isOTPExpired(OTPMap.get(phonenumber).getExpired())) {
                    System.out.println("ERROR: OTP expired, send get another one");
                    OTPMap.remove(phonenumber);
                    return "ERROR: OTP expired, send get another one ";
                }
                System.out.println("ERROR: OTP matches, can do transaction or get password or stuff");
                OTPMap.remove(phonenumber);

                return "OTP matches, can do transaction or get password or stuff";
            } else {
                return "ERROR: invalid OTP";
            }
        } catch (Exception e) {
            System.out.println("something wrong with phone, maybe not correct return, ");
            return "ERROR: "+e.getMessage();
        }

    }

    private String generateOTP(){// 6 digit
        return new DecimalFormat(OTPpattern)
                .format(new Random().nextInt(integerBound));
    }

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

    public Map<String, OTPAttribute> getOTPMap(){
        return this.OTPMap;
    }

    public long getOTP_TIMEOUT_SECOND() {
        return OTP_TIMEOUT_SECOND;
    }
    private LocalDateTime getExpiredTime(){
        return LocalDateTime.now().plusSeconds(OTP_TIMEOUT_SECOND);
        //return new Date(System.currentTimeMillis()) ;
    }
    private boolean isOTPExpired(LocalDateTime expired){
        return expired.isBefore( LocalDateTime.now());
    }
}
