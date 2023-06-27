package app.vinhomes.security.email.email_service;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.security.email.email_dto.TokenEntity;

import app.vinhomes.service.AccountService;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService   {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private TokenService tokenService;
    private final Map<String, TokenEntity> tokenEntityMap = new HashMap<>();
    public String sendSimpleVerficationEmail(Account account){
        TokenEntity tokenEntity = tokenService.createTokenEntity(account.getEmail());
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(account.getEmail());
            mailMessage.setSubject("verification token:");
            mailMessage.setText(tokenEntity.getTokenvalue());
            tokenEntityMap.put(account.getEmail(),tokenEntity);
            // Sending the mail
            String urlParam = "emailTo="+account.getEmail().toString().trim()+"&tokenValue="+tokenEntity.getTokenvalue();
            mailMessage.setText("http://localhost:8080/mail/verification?"+urlParam);
            javaMailSender.send(mailMessage);
            return "send success";
        }
        // Catch block to handle the exceptions
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error while Sending Mail";
        }
    }
    public String sendMailWithTemplate(Account account){
        TokenEntity tokenEntity = tokenService.createTokenEntity(account.getEmail());
        try {

            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mailMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            String urlParam = "emailTo="+account.getEmail().toString().trim()+"&tokenValue="+tokenEntity.getTokenvalue();
            String completedLink  = "http://localhost:8080/mail/verification?"+urlParam;
            Context context = new Context();
            context.setVariable("username",account.getAccountName());
            context.setVariable("firstname",account.getFirstName());
            context.setVariable("lastname",account.getLastName());
            context.setVariable("url",completedLink);
            helper.setFrom(sender);
            helper.setTo(account.getEmail());
            mailMessage.setSubject("Test email Verification token");
            String html = templateEngine.process("verificationTemplate",context);
            helper.setText(html,true);
            tokenEntityMap.put(account.getEmail(),tokenEntity);
            javaMailSender.send(mailMessage);
            return "send success";
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error while Sending Mail";
        }
    }

//    public String sendSimpleVerficationEmail(String emailTo){// for testing, can enter real email to test on postman
//        TokenEntity tokenEntity = tokenService.createTokenEntity(emailTo);
//        try {
//            SimpleMailMessage mailMessage = new SimpleMailMessage();
//            mailMessage.setFrom(sender);
//            mailMessage.setTo(emailTo);
//            mailMessage.setSubject("verification token:");
//
//            mailMessage.setText(tokenEntity.getTokenvalue());
//            tokenEntityMap.put(emailTo,tokenEntity);
//            // Sending the mail
//            String urlParam = "emailTo="+emailTo.trim()+"&tokenValue="+tokenEntity.getTokenvalue();
//            mailMessage.setText("http://localhost:8080/mail/verification?"+urlParam);
//
//            javaMailSender.send(mailMessage);
//            return "send success";
//        }
//
//        // Catch block to handle the exceptions
//
//        catch (Exception e){
//
//            System.out.println(e.getMessage());
//            return "Error while Sending Mail";
//        }
//    }

    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        System.out.println(siteURL);
        System.out.println(request.getServletPath());
        return siteURL.replace(request.getServletPath(), "");
    }
    public String checkIfTokenValeValid(String emailTo,String tokenValue){
        Map<String,TokenEntity> getMap = getTokenEntityMap();
        TokenEntity getTokenEntity = getMap.get(emailTo);
        if(getTokenEntity == null){
            return "ERROR: token has been delete, you have already been verified";
        }
        System.out.println(getTokenEntity.getEmail()+", "+getTokenEntity.getTokenvalue());
        if(tokenService.checkIfExpired(getTokenEntity)){
            getMap.remove(emailTo);
            return "ERROR: token expired";// out of date
        }else{
            if(getTokenEntity.getTokenvalue().equals(tokenValue)){
                boolean isUpdateSuccess = updateAccountStatus(emailTo);
                if(isUpdateSuccess){
                    getMap.remove(emailTo);
                    return "SUCCESS";
                }
                return "ERROR";
            }else{
                return "ERROR: token not match";
            }
        }
    }
    private boolean updateAccountStatus(String emailTo){
        try{
            Account getAccount = accountRepository.findByEmailEquals(emailTo);
            if(getAccount != null){
                getAccount.setIsEnable(1);
                accountRepository.save(getAccount);
                return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }
    public Map<String,TokenEntity> getTokenEntityMap(){
        return this.tokenEntityMap;
    }


}
