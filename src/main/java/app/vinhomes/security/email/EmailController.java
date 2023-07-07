package app.vinhomes.security.email;


import app.vinhomes.security.email.email_service.EmailService;

import app.vinhomes.security.email.email_service.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/mail")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenService tokenService;
    private String emailParameter = "email";
    @PostMapping("/sendMail")
    public ResponseEntity<?> sendMail(HttpServletRequest request) {
        try{
            //emailService.sendSimpleVerficationEmail(request.getParameter("email"));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("somethingwrong");
        }
        System.out.println("yes nothing broke Yet");
        return ResponseEntity.ok().body("yes send success");
    }

//    @GetMapping(value = "/verification")
//    public ResponseEntity<String> checkEmailVerification(HttpServletRequest request){
//        try{
//            //lay tu link bam tu mail
//            System.out.println("inside email Verification");
//            String emailTo = request.getParameter("emailTo");
//            String tokenValue = request.getParameter("tokenValue");
//            String message = emailService.checkIfTokenValeValid(emailTo,tokenValue   );
//            if(message.equals("SUCCESS") == false){
//                System.out.println(message);
//                return ResponseEntity.ok().body("invalid validation");
//            }
//            else{
//                //TODO : do something after check mail succeses
//                System.out.println(message);
//
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("yes validated success");
//            }
//        }catch (Exception e){
//            System.out.println("error inside emailController: "+e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR on server");
//        }
//    }

    @GetMapping
    public ResponseEntity<?> getTokenMap(){
        return ResponseEntity.ok().body(emailService.getTokenEntityMap()    );
    }

    @GetMapping(value = "/getUrl")
    public String testGetCurrentUrl(HttpServletRequest request){
        return emailService.getSiteURL(request);
    }

}
