package app.vinhomes.event.listener_storage;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.worker.CancelRequest;
import app.vinhomes.event.event_storage.SendEmailCancelOrder;
import app.vinhomes.event.event_storage.SendEmailOnCreateAccount;
import app.vinhomes.security.email.email_service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;

public class OnCancelRequest {
    @Value("${mail.mailType.cancelRequest}")
    private String CANCEL_REQUEST;
    @Autowired
    private EmailService emailService;
    @EventListener
    public void sendEmailWorker_cancelRequest (SendEmailCancelOrder event){
        CancelRequest getCancelRequest = event.getCancelRequest();
        emailService.sendMailWithTemplate(CANCEL_REQUEST,getCancelRequest);
    }
}
