package app.vinhomes.controller;


import app.vinhomes.SECURITY.Authentication.AuthenticationRequest;
import app.vinhomes.SECURITY.Authentication.AuthenticationResponse;
import app.vinhomes.SECURITY.Authentication.AuthenticationService;
import app.vinhomes.SECURITY.Config.SimpleSuccessHandler;
import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController //Use for API
@RequestMapping
public class AuthorizationAPI {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private SimpleSuccessHandler successHandler;
    @PostMapping(value = "/login",consumes = MediaType.ALL_VALUE)
    public String loginHttp( HttpServletRequest request, HttpServletResponse response) {
        System.out.println("inside login HTTP");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        AuthenticationRequest requestForToken  = new AuthenticationRequest(username,password);
        System.out.println("about to auth user name ");
        try{
           Account loginStatus = authenticationService.authenticate(requestForToken);
            if(loginStatus == null){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                return "fail";
            }
//            return ResponseEntity.status(HttpStatus.OK).body("suucess login");
//            request.getSession().setAttribute("loginedUser", loginStatus);
            return "success";
        }catch (Exception e){
            System.out.println("Login fail, some stuff happen");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            return "fail";
        }
    }
}
