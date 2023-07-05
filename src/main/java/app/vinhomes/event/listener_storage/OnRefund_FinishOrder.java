package app.vinhomes.event.listener_storage;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.event.event_storage.SendEmailOnRefund_OnFinishOrder;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.security.email.email_service.EmailService;
import app.vinhomes.security.esms.otp_service.ESMSservice;
import app.vinhomes.security.esms.otp_service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OnRefund_FinishOrder {
    @Value("${mail.mailType.adminRefundTransaction}")
    private String ADMIN_REFUNDTRANSACTION_MAIL;
    @Value("${mail.mailType.orderFinish}")
    private String ORDERFINISH_MAIL;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ESMSservice ESMSService;
    @Autowired
    private OTPService otpService;
    @Autowired
    private PhoneRepository phoneRepository;
    @EventListener
    public boolean sendEmail_AdminRefundTransaction(SendEmailOnRefund_OnFinishOrder event){
        Account getAccount = event.getAccount();
        Transaction getTransaction = event.getTransaction();
        String getPhonenumber = getTransaction.getOrder().getPhoneNumber();
        try{
            System.out.println("inside listener send email async ");
            if(event.isFinish() == false){
                emailService.sendMailWithTemplate(getAccount,ADMIN_REFUNDTRANSACTION_MAIL,getTransaction);
                //ESMSService.sendGetJSON(getPhonenumber,"your order has been refunded, please check your bank account");
                return true;
            }
            return false;
        }catch (Exception e){
            System.out.println("error in listener send email: "+ e.getMessage());
            return false;
        }
    }
    @EventListener
    public boolean sendEmail_OnOrderFinish(SendEmailOnRefund_OnFinishOrder event){
        Account getAccount = event.getAccount();
        Transaction getTransaction = event.getTransaction();
        String getPhonenumber = getTransaction.getOrder().getPhoneNumber();
        try{
            System.out.println("inside listener send email async ");
            if(event.isFinish()){
                emailService.sendMailWithTemplate(getAccount,ORDERFINISH_MAIL,getTransaction);
                //ESMSService.sendGetJSON(getPhonenumber,"your order has been completed, thank you for using our service, please give us feedback on website");
                return true;
            }
            return false;
        }catch (Exception e){
            System.out.println("error in listener send email: "+ e.getMessage());
            return false;
        }
    }

}
