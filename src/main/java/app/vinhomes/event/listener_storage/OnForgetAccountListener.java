package app.vinhomes.event.listener_storage;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.event.event_storage.SendEmailForgetAccount;
import app.vinhomes.event.event_storage.SendSmsForgetAccount;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.security.email.email_service.EmailService;
import app.vinhomes.security.esms.otp_dto.EnumOTPStatus;
import app.vinhomes.security.esms.otp_dto.OTPResponseStatus;
import app.vinhomes.security.esms.otp_service.ESMSservice;
import app.vinhomes.security.esms.otp_service.OTPService;
import app.vinhomes.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OnForgetAccountListener {

    @Value("${mail.mailType.forgetAccount}")
    private String FORGETACCOUNT_MAIL;

    @Autowired
    private AccountService accountService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ESMSservice ESMSService;
    @Autowired
    private OTPService otpService;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private AccountRepository accountRepository;

    @EventListener
    public void sendEmailOnForgetAccount(SendEmailForgetAccount event) {
        String getEmail = event.getEmailTo();
        try {
            System.out.println("inside listener send email async forget Account  ");
            Account getAccount = accountRepository.findByEmailEquals(getEmail);
            emailService.sendMailWithTemplate(getAccount, FORGETACCOUNT_MAIL);
            System.out.println("finish sending email forget Account ");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @EventListener
    public void sendSmsOnForgetAccount(SendSmsForgetAccount event) {
        String getPhonenumber = event.getPhonenumberTo();
        try {
            System.out.println("inside listener send sms async forget Account  ");
            List<Phone> getPhonesList = phoneRepository.findByNumber(getPhonenumber);
            Phone getPhone = getPhonesList.get(0);

            Account account = getPhone.getAccount();
            String getGeneratedPassword = accountService.changePassword_ForgetAccountService(account);
            System.out.println(getPhone.getNumber());
            StringBuilder bulidMessage = new StringBuilder("");
            bulidMessage.append("this is your account and generated password, " +
                    "please change it immediately when goin back." +
                    "username: ");
            bulidMessage.append(account.getAccountName());
            bulidMessage.append(" | password(generated): ");
            bulidMessage.append(getGeneratedPassword);
            System.out.println(bulidMessage.toString());
            System.out.println("in sending sms forget Account ");
            //ESMSService.sendGetJSON(getPhone.getNumber(),bulidMessage.toString());
        } catch (Exception e) {
            System.out.println("error in listener send sms forget Account: " + e.getMessage());
        }
    }
}
