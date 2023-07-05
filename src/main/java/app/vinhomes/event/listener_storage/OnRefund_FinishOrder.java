package app.vinhomes.event.listener_storage;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Transaction;
import app.vinhomes.event.event_storage.SendEmailOnRefund_OnFinishOrder;
import app.vinhomes.security.email.email_service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class OnRefund_FinishOrder {
    @Value("${mail.mailType.adminRefundTransaction}")
    private String ADMIN_REFUNDTRANSACTION_MAIL;
    @Value("${mail.mailType.orderFinish}")
    private String ORDERFINISH_MAIL;
    @Autowired
    private EmailService emailService;
    @EventListener
    public boolean sendEmail_AdminRefundTransaction(SendEmailOnRefund_OnFinishOrder event){
        Account getAccount = event.getAccount();
        Transaction getTransaction = event.getTransaction();
        try{
            System.out.println("inside listener send email async ");
            if(event.isFinish() == false){
                emailService.sendMailWithTemplate(getAccount,ADMIN_REFUNDTRANSACTION_MAIL,getTransaction);
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
        try{
            System.out.println("inside listener send email async ");
            if(event.isFinish()){
                emailService.sendMailWithTemplate(getAccount,ORDERFINISH_MAIL,getTransaction);
                return true;
            }
            return false;
        }catch (Exception e){
            System.out.println("error in listener send email: "+ e.getMessage());
            return false;
        }
    }

}
