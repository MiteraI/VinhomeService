package app.vinhomes.event.listener_storage;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Transaction;
import app.vinhomes.event.event_storage.SendEmailAdminRefund;
import app.vinhomes.event.event_storage.SendEmailOnCreateAccount;
import app.vinhomes.security.email.email_service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;

public class OnAdminRefund {
    @Value("${mail.mailType.adminRefundTransaction}")
    private String ADMIN_REFUNDTRANSACTION_MAIL;
    @Autowired
    private EmailService emailService;
    @EventListener
    public boolean sendEmail_AdminRefundTransaction(SendEmailAdminRefund event){
        Account getAccount = event.getAccount();
        Transaction getTransaction = event.getTransaction();
        try{
            System.out.println("inside listener send email async ");
            emailService.sendMailWithTemplate(getAccount,ADMIN_REFUNDTRANSACTION_MAIL);
            return true;
        }catch (Exception e){
            System.out.println("error in listener send email: "+ e.getMessage());
            return false;
        }
    }
}
