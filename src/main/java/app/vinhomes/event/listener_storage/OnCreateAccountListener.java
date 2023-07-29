package app.vinhomes.event.listener_storage;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.event.event_storage.SendEmailOnCreateAccount;
import app.vinhomes.event.event_storage.SendSmsOnCreateAccount;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.security.email.email_service.EmailService;
import app.vinhomes.security.esms.otp_dto.EnumOTPStatus;
import app.vinhomes.security.esms.otp_dto.OTPResponseStatus;
import app.vinhomes.security.esms.otp_service.ESMSservice;
import app.vinhomes.security.esms.otp_service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class OnCreateAccountListener {

    @Value("${mail.mailType.verification}")
    private String VERIFICATION_MAIL;

    @Autowired
    private EmailService emailService;
    @Autowired
    private ESMSservice ESMSService;
    @Autowired
    private OTPService otpService;
    @Autowired
    private PhoneRepository phoneRepository;
    @EventListener
    public boolean sendEmailVerification (SendEmailOnCreateAccount event){
        Account getAccount = event.getFreshAccount();
        try{
            System.out.println("inside listener send email async ");
            emailService.sendMailWithTemplate(getAccount,VERIFICATION_MAIL);
            System.out.println("finish sending fucking mail >:(");
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
            Phone getPhone = phoneRepository.findByAccount_AccountId(getAccount.getAccountId()).get(0);
            System.out.println(getPhone.getNumber());
            OTPResponseStatus OTP = otpService.generateOTPCode_Message(getPhone.getNumber());
            if(OTP.getStatus().equals(EnumOTPStatus.CREATED)){
                System.out.println("in sending sms :"+ getPhone.getNumber());
                ESMSService.sendGetJSON(getPhone.getNumber().trim(),OTP.getMessage());
                return true;
            }else{
                System.out.println("ERROR sending sms ");
                return false;
            }
        }catch (Exception e){
            System.out.println("error in listener send sms: "+ e.getMessage());
            return false;
        }
    }
}
