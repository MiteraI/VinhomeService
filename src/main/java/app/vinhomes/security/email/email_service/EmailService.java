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

    public String sendSimpleVerficationEmail(String emailTo){// for testing, can enter real email to test on postman
        TokenEntity tokenEntity = tokenService.createTokenEntity(emailTo);

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(emailTo);
            mailMessage.setSubject("verification token:");

            mailMessage.setText(tokenEntity.getTokenvalue());
            tokenEntityMap.put(emailTo,tokenEntity);
            // Sending the mail
            String urlParam = "emailTo="+emailTo.trim()+"&tokenValue="+tokenEntity.getTokenvalue();
            mailMessage.setText("http://localhost:8080/mail/verification?"+urlParam);

            javaMailSender.send(mailMessage);
            return "send success";
        }

        // Catch block to handle the exceptions

        catch (Exception e){

            System.out.println(e.getMessage());
            return "Error while Sending Mail";
        }
    }

    public String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        System.out.println(siteURL);
        System.out.println(request.getServletPath());
        return siteURL.replace(request.getServletPath(), "");
    }
    public String checkIfTokenValeValid(String emailTo,String tokenValue){
        Map<String,TokenEntity> getMap = getTokenEntityMap();

        TokenEntity getTokenEntity = getMap.get(emailTo);
        System.out.println(getTokenEntity.getEmail()+", "+getTokenEntity.getTokenvalue());
        if(tokenService.checkIfExpired(getTokenEntity)){
            return "ERROR: token expired";// out of date
        }else{
            if(getTokenEntity.getTokenvalue().equals(tokenValue)){
                return "SUCCESS";
            }else{
                return "ERROR: token not match";
            }
        }
    }
    public Map<String,TokenEntity> getTokenEntityMap(){
        return this.tokenEntityMap;
    }


}
