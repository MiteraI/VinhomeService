
package app.vinhomes.security.email.email_service;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.worker.CancelRequest;
import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.security.email.email_dto.TokenEntity;
import app.vinhomes.entity.order.Service;

import app.vinhomes.service.AccountService;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class EmailService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private AccountService accountService;
    @Value("${spring.mail.username}")
    private String sender;
    @Value("${mail.mailType.verification}")
    private String VERIFICATION_MAIL;
    @Value("${mail.mailType.forgetAccount}")
    private String FORGETACCOUNT_MAIL;
    @Value("${mail.mailType.orderFinish}")
    private String ORDERFINISH_MAIL;
    @Value("${mail.mailType.adminRefundTransaction}")
    private String ADMIN_REFUNDTRANSACTION_MAIL;
    @Value("${mail.mailType.acceptLeaveReport}")
    private String LEAVE_REPORT;
    @Value("${mail.mailType.cancelRequest}")
    private String CANCEL_REQUEST;
    @Autowired
    private TokenService tokenService;
    private final Map<String, TokenEntity> tokenEntityMap = new HashMap<>();

    public String sendMailWithTemplate(Account account, String mailType) {
        try {
            if (mailType.equals(VERIFICATION_MAIL)) {
                verificationMailBuilder(account);
            } else if (mailType.equals(FORGETACCOUNT_MAIL)) {
                forgetAccountMailBuilder(account);
            }
            else {
                return "ERROR";
            }
            return "SUCCESS send email";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "ERROR while Sending Mail";
        }
    }
    public String sendMailWithTemplate(Account account, String mailType, LeaveReport report) {
        try {
            if (mailType.equals(LEAVE_REPORT)) {
                onLeaveReportAccept_Denied(account,report);
            }else {
                return "ERROR";
            }
            return "SUCCESS send email";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "ERROR while Sending Mail";
        }
    }
    public String sendMailWithTemplate(String mailType, CancelRequest cancelRequest) {
        try {
            if (mailType.equals(CANCEL_REQUEST)) {
                onCancelRequest_Worker(cancelRequest);
            }
            else {
                return "ERROR";
            }
            return "SUCCESS send email";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "ERROR while Sending Mail";
        }
    }
    public String sendMailWithTemplate(Account account, String mailType, Transaction transaction) {
        try {
            if (mailType.equals(ADMIN_REFUNDTRANSACTION_MAIL)) {
                System.out.println("admin-refund-transaction-mail TYPE");
                 this.admin_refundTransactionBuilder(account, transaction);
            } else if (mailType.equals(ORDERFINISH_MAIL)) {
                System.out.println("orderfinish_mail");
                 this.onOrderFinish_ConfirmByWorker(account, transaction);
            } else {
                return "ERROR";
            }
            return "SUCCESS";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "ERROR while Sending Mail";
        }
    }


    private void verificationMailBuilder(Account account) {
        try {
            System.out.println("EmailService executing verificationMailBuilder");
            TokenEntity tokenEntity = tokenService.createTokenEntity(account.getEmail());
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            String urlParam = "emailTo=" + account.getEmail().toString().trim() + "&tokenValue=" + tokenEntity.getTokenvalue();
            String completedLink = "http://localhost:8080/mail/verification?" + urlParam;
            System.out.println(completedLink);
            Context context = new Context();
            context.setVariable("username", account.getAccountName());
            context.setVariable("firstname", account.getFirstName());
            context.setVariable("lastname", account.getLastName());
            context.setVariable("url", completedLink);
            helper.setFrom(sender);
            helper.setTo(account.getEmail());
            mailMessage.setSubject("Test email Verification token");
            String html = templateEngine.process("MAIL_verification", context);
            helper.setText(html, true);
            tokenEntityMap.put(account.getEmail(), tokenEntity);
            javaMailSender.send(mailMessage);
        } catch (SendFailedException e) {
            System.out.println("ERROR this mail cannot be sent" + e.getMessage());
        } catch (MessagingException e) {
            System.out.println("ERROR inside verification mail builder" + e.getMessage());
        }
    }

    private void forgetAccountMailBuilder(Account account) {
        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            Context context = new Context();
            context.setVariable("username", account.getAccountName());
            context.setVariable("firstname", account.getFirstName());
            context.setVariable("lastname", account.getLastName());
            String getGeneratedPassword = accountService.changePassword_ForgetAccountService(account);
            context.setVariable("password", getGeneratedPassword);
            context.setVariable("image", "imageSource");
            helper.setFrom(sender);
            helper.setTo(account.getEmail());
            mailMessage.setSubject("forget Account");
            String html = templateEngine.process("MAIL_forgetAccount", context);
            helper.setText(html, true);
            ClassPathResource getImage = new ClassPathResource("src/assets/images/service-1.jpg");
            Resource resource1 = getImage;
            helper.addInline("image.png", resource1, "image/png");
            System.out.println("pass image source, about to send email");
            javaMailSender.send(mailMessage);
        } catch (SendFailedException e) {
            System.out.println("ERROR this mail cannot be sent" + e.getMessage());
        } catch (MessagingException e) {
            System.out.println("ERROR inside verification mail builder" + e.getMessage());
        }
    }

    private void admin_refundTransactionBuilder(Account account, Transaction transaction) {
        try {
            Order getOrder = transaction.getOrder();
            Service getService = getOrder.getService();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String getDate = format.format(new Date(System.currentTimeMillis()));
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            Context context = new Context();
            context.setVariable("Account", account);
            context.setVariable("Order", getOrder);
            context.setVariable("Transaction", transaction);
            context.setVariable("Service", getService);
            context.setVariable("RefundTime", getDate);
            helper.setFrom(sender);
            helper.setTo(account.getEmail());
            mailMessage.setSubject("Manager has refunded this Order");
            String html = templateEngine.process("MAIL_adminCancelOrder", context);
            helper.setText(html, true);
            System.out.println("now send mail");
            javaMailSender.send(mailMessage);
        } catch (SendFailedException e) {
            System.out.println("ERROR this mail cannot be sent: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void onOrderFinish_ConfirmByWorker(Account account, Transaction transaction) {
        try {
            Order getOrder = transaction.getOrder();
            Service getService = getOrder.getService();
            List<Account> getWorkerList = getOrder.getSchedule().getWorkers();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String getDate = format.format(new Date(System.currentTimeMillis()));
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            Context context = new Context();
            context.setVariable("Account", account);
            context.setVariable("Order", getOrder);
            context.setVariable("Transaction", transaction);
            context.setVariable("Service", getService);
            context.setVariable("RefundTime", getDate);
            context.setVariable("WorkersList", getWorkerList);
            helper.setFrom(sender);
            helper.setTo(account.getEmail());
            mailMessage.setSubject("worker has confirm this order");
            String html = templateEngine.process("MAIL_onConfirmOrder", context);
            helper.setText(html, true);
            javaMailSender.send(mailMessage);
        } catch (SendFailedException e) {
            System.out.println("ERROR this mail cannot be sent: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void onLeaveReportAccept_Denied(Account account,LeaveReport report) {
        try {
            System.out.println("inside sending email on accept leave reprot");
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            Context context = new Context();
            context.setVariable("Account",account);
            context.setVariable("Report",report);
            if(report.getStatus() == 0 ){
                context.setVariable("Status","Reject");
            }else{
                context.setVariable("Status","Accept");
            }
            helper.setFrom(sender);
            helper.setTo(account.getEmail());
            mailMessage.setSubject("admin accept leave report");
            String html = templateEngine.process("MAIL_LeaveReportConfirmation", context);
            helper.setText(html, true);
            javaMailSender.send(mailMessage);
        } catch (SendFailedException e) {
            System.out.println("ERROR this mail cannot be sent: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void onCancelRequest_Worker( CancelRequest cancelRequest){
        try {
            System.out.println("inside sending email on cancel order");
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            Context context = new Context();
            context.setVariable("status",cancelRequest.getStatus().name());
            context.setVariable("Account",cancelRequest.getWorker());
            context.setVariable("Order",cancelRequest.getOrder());
            helper.setFrom(sender);
            helper.setTo(cancelRequest.getWorker().getEmail());
            mailMessage.setSubject("cancel request from manager ");
            String html = templateEngine.process("MAIL_cancelRequestWorker", context);
            helper.setText(html, true);
            javaMailSender.send(mailMessage);
        } catch (SendFailedException e) {
            System.out.println("ERROR this mail cannot be sent: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        System.out.println(siteURL);
        System.out.println(request.getServletPath());
        return siteURL.replace(request.getServletPath(), "");
    }

    public String checkIfTokenValeValid(String emailTo, String tokenValue) {
        Map<String, TokenEntity> getMap = getTokenEntityMap();
        TokenEntity getTokenEntity = getMap.get(emailTo);
        if (getTokenEntity == null) {
            return "ERROR: token has been delete, you have already been verified";
        }
        System.out.println(getTokenEntity.getEmail() + ", " + getTokenEntity.getTokenvalue());
        if (tokenService.checkIfExpired(getTokenEntity)) {
            getMap.remove(emailTo);
            return "ERROR: token expired";// out of date
        } else {
            if (getTokenEntity.getTokenvalue().equals(tokenValue)) {
                boolean isUpdateSuccess = updateAccountStatus(emailTo);
                if (isUpdateSuccess) {
                    getMap.remove(emailTo);
                    return "SUCCESS";
                }
                return "ERROR";
            } else {
                return "ERROR: token not match";
            }
        }
    }

    private boolean updateAccountStatus(String emailTo) {
        try {
            Account getAccount = accountRepository.findByEmailEquals(emailTo);
            if (getAccount != null) {
                getAccount.setIsEnable(1);
                accountRepository.save(getAccount);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Map<String, TokenEntity> getTokenEntityMap() {
        return this.tokenEntityMap;
    }


}

