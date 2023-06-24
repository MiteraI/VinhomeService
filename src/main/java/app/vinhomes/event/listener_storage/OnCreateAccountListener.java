package app.vinhomes.event.listener_storage;

import app.vinhomes.entity.Account;
import app.vinhomes.event.event_storage.SendEmailOnCreateAccount;
import app.vinhomes.event.event_storage.SendSmsOnCreateAccount;
import app.vinhomes.security.email.email_service.EmailService;
import app.vinhomes.security.esms.otp_dto.EnumOTPStatus;
import app.vinhomes.security.esms.otp_dto.OTPResponseStatus;
import app.vinhomes.security.esms.otp_service.ESMSservice;
import app.vinhomes.security.esms.otp_service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OnCreateAccountListener {
    @Autowired
    private EmailService emailService;
    @Autowired
    private ESMSservice ESMSService;
    @Autowired
    private OTPService otpService;
    @EventListener
    public boolean sendEmailVerification (SendEmailOnCreateAccount event){
        Account getAccount = event.getFreshAccount();
        try{
            System.out.println("inside listener send email async ");
            emailService.sendSimpleVerficationEmail(getAccount);
            return true;
        }catch (Exception e){
            System.out.println("error in listener send email: "+ e.getMessage());
            return false;
        }
    }

    @EventListener
    public boolean sendSmsVerification (SendSmsOnCreateAccount event){
        Account getAccount = event.getFreshAccount();
        try{
            System.out.println("inside listener send sms async ");
            return true;
//            String phonenumber =getAccount.getPhones().stream().findFirst().get().getNumber() ;
//            System.out.println("inside listener send sms async ");
//            OTPResponseStatus OTP = otpService.generateOTPCode_Message(phonenumber);
//            if(OTP.getStatus().equals(EnumOTPStatus.CREATED)){
//                ESMSService.sendGetJSON(phonenumber,OTP.getMessage());
//                return true;
//            }else{
//                System.out.println("ERROR sending sms ");
//                return false;
//            }
        }catch (Exception e){
            System.out.println("error in listener send sms: "+ e.getMessage());
            return false;
        }

    }
}
