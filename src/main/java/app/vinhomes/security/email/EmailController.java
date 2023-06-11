package app.vinhomes.security.email;


import app.vinhomes.security.email.email_service.EmailService;
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
    private String emailParameter = "email";
    @PostMapping("/sendMail")
    public ResponseEntity<?> sendMail(HttpServletRequest request) {
        try{
            emailService.sendSimpleMail(request.getParameter("email"));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("somethingwrong");
        }
        System.out.println("yes nothing broke Yet");
        return ResponseEntity.ok().body("yes send success");
    }
    @GetMapping
    public ResponseEntity<?> getTokenMap(){
        return ResponseEntity.ok().body(emailService.getTokenEntityMap()    );
    }

}
