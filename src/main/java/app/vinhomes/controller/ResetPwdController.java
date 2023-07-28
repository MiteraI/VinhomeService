package app.vinhomes.controller;


import app.vinhomes.CustomerSessionListener;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import com.azure.core.annotation.Get;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/resetpassword")
public class ResetPwdController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomerSessionListener customerSessionListener;
    @Autowired
    private PhoneRepository phoneRepository;

    @RequestMapping(value = "/resetPwd/{uid}", method = RequestMethod.POST)
    public ResponseEntity<String> resetPwd(@PathVariable("uid") Long uid, @RequestBody JsonNode data, HttpServletRequest request){
        System.out.println(uid);
        Account acc = accountRepository.findById(uid).get();
        String newPwd = data.get("new_password").asText().trim();
        String oldPwd = data.get("old_password").asText().trim();
        try{
            if(passwordEncoder.matches(oldPwd, acc.getPassword())){
                acc.setPassword(passwordEncoder.encode(newPwd));
                accountRepository.save(acc);
                if(request.getSession(false) != null){
                    HttpSession session = request.getSession(false);
                    if(session.getAttribute("loginedUser") != null){
                        session.setAttribute("loginedUser", acc);
                    }
                }
                return ResponseEntity.status(HttpStatus.OK).body("good");
            }else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("old password is wrong");
            }

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("your password suck");
        }
    }

}