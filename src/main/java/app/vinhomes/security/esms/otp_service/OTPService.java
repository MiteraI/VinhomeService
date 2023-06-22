package app.vinhomes.security.esms.otp_service;

import app.vinhomes.security.esms.otp_dto.OTPAttribute;
import app.vinhomes.security.esms.otp_dto.OTPStatus;
import app.vinhomes.security.esms.otp_dto.PasswordResetResponseDTO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
//@SuppressFBWarnings(value = "DMI_RANDOM_USED_ONLY_ONCE")
public class OTPService {
    private final String OTPpattern = "000000";
    private final int integerBound = 999999;
    private final Map<String, OTPAttribute> OTPMap = new HashMap<>();
    private final long expriredTimeSecond = 1000*60 ;
    public PasswordResetResponseDTO OTPPasswordReset(String phonenumber) {
        try {
            boolean checkIfPhonenumberExist = checkIfMoreThanOneOrExistOneOTP(phonenumber);
            if(checkIfPhonenumberExist){
                OTPMap.remove(phonenumber);
            }
            String OTP = generateOTP();
            String messageBody = "this is a message from spring boot, your OTP" +
                    "is:   "+ OTP;
            System.out.println(messageBody);
            OTPAttribute otpAttribute = new OTPAttribute(OTP,getExpired());
            OTPMap.put(phonenumber,otpAttribute);
            return  new PasswordResetResponseDTO(OTPStatus.CREATED,messageBody);
        }catch (Exception e){
            return  new PasswordResetResponseDTO(OTPStatus.FAIL,e.getMessage());
        }
    }

    public String validateOTP(String incomingOTP,String phonenumber) {
        try {
            if (incomingOTP.equals(OTPMap.get(phonenumber).getOneTimePassword())) {
                System.out.println("time set expired " + OTPMap.get(phonenumber).getExpired());
                System.out.println("time now" + new Date());
                if (isOTPExpired(OTPMap.get(phonenumber).getExpired())) {
                    OTPMap.remove(phonenumber);
                    return "OTP expired, send get another one ";
                }
                OTPMap.remove(phonenumber);
                return "OTP matches, can do transaction or get password or stuff";
            } else {
                return "ERROR, invalid OTP, try choose resent OTP";
            }
        } catch (Exception e) {
            System.out.println("something wrong with phone, maybe not correct return, ");
            return e.getMessage();
        }

    }

    private String generateOTP(){// 6 digit
        return new DecimalFormat(OTPpattern)
                .format(new Random().nextInt(integerBound));
    }
//    private String generateOTP() {
//        SecureRandom secRandom = new SecureRandom();
//
//        byte[] result = new byte[32];
//        secRandom.nextBytes(result);
//        return Hex.encodeHexString(result);
//    }
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
    private boolean isOTPExpired(Date expired){
        return expired.before(new Date());
    }
    public Map<String, OTPAttribute> getOTPMap(){
        return this.OTPMap;
    }

}
