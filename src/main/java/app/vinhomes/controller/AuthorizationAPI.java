package app.vinhomes.controller;


import app.vinhomes.Security.Authentication.AuthenticationRequest;
import app.vinhomes.Security.Authentication.AuthenticationService;
import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //Use for API
@RequestMapping(value = "")
public class AuthorizationAPI {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @PostMapping(value = "/login",consumes = MediaType.ALL_VALUE)
    public String login( HttpServletRequest request, HttpServletResponse response) {
        System.out.println("inside login");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        AuthenticationRequest requestForToken  = new AuthenticationRequest(username,password);
        try{
            Account loginStatus = authenticationService.authenticate(requestForToken);
            if(loginStatus == null){
                return "fail";
            }
            return "success";
        }catch (Exception e){
            System.out.println("Login fail, some stuff happen");
            return "fail";
        }
    }
}
