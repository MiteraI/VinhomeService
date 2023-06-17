package app.vinhomes.security.email.email_service;

import app.vinhomes.entity.Account;
import app.vinhomes.security.email.email_dto.TokenEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService   {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;
    @Autowired
    private TokenService tokenService;
    private final Map<String, TokenEntity> tokenEntityMap = new HashMap<>();
    public String sendSimpleMail(Account account){
        TokenEntity tokenvalue = tokenService.createTokenEntity(account.getEmail());
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(account.getEmail());
            mailMessage.setSubject("verification token:");
            mailMessage.setText(tokenvalue.getTokenvalue());
            tokenEntityMap.put(account.getEmail(),tokenvalue);
            // Sending the mail
            javaMailSender.send(mailMessage);
            return "send success";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error while Sending Mail";
        }
    }
    public String sendSimpleMail(String emailTo){
        TokenEntity tokenvalue = tokenService.createTokenEntity(emailTo);
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(emailTo);
            mailMessage.setSubject("verification token:");
            mailMessage.setText(tokenvalue.getTokenvalue());
            tokenEntityMap.put(emailTo,tokenvalue);
            // Sending the mail
            javaMailSender.send(mailMessage);
            return "send success";
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error while Sending Mail";
        }
    }
    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
    public Map<String,TokenEntity> getTokenEntityMap(){
        return this.tokenEntityMap;
    }
}
