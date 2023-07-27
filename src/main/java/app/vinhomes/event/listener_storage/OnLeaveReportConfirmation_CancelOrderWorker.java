package app.vinhomes.event.listener_storage;





import app.vinhomes.entity.Account;
import app.vinhomes.entity.worker.Leave;
import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.event.event_storage.SendEmailForgetAccount;
import app.vinhomes.event.event_storage.SendEmail_LeaveReportConfirmation;
import app.vinhomes.security.email.email_service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;

public class OnLeaveReportConfirmation_CancelOrderWorker {
    @Autowired
    private EmailService emailService;
    @Value("${mail.mailType.acceptLeaveReport}")
    private String LEAVE_REPORT;
    @EventListener
    public void sendEmailOnForgetAccount(SendEmail_LeaveReportConfirmation event) {
        try{
            LeaveReport getReport = event.getGetReport();
            emailService.sendMailWithTemplate(event.getAccount(),LEAVE_REPORT,getReport);
        }catch (Exception e){

        }
    }
    @EventListener
    public void sendEmailOnCancelOrder(){

    }
}
