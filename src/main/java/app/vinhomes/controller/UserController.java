package app.vinhomes.controller;


import app.vinhomes.Register.CreateAccountError;
import app.vinhomes.Register.Register;
import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/UserRestController")
public class UserController {
    @Autowired
    private AccountRepository account;
    @PostMapping(value = "/createAccount",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateAccountError> createAccount(@RequestBody JsonNode request){
        System.out.println("inside create account");
        String username, password, email, firstname, lastname,phonenumber, date,address;
        username = Register.checkUsername(request.get("txtUsername").asText());System.out.println(username);
        password = Register.checkPassword(request.get("txtPassword").asText());System.out.println(password);
        email = Register.checkEmail(request.get("txtEmail").asText());System.out.println(email);
        firstname = Register.checkFirstname( request.get("txtFirstname").asText());System.out.println(firstname);
        lastname = Register.checkLastname(request.get("txtLastname").asText());System.out.println(lastname);
        date = Register.checkDate(request.get("txtDate").asText());System.out.println(date);
        phonenumber = Register.checkPhoneNumber(request.get("txtPhonenumber").asText());System.out.println(phonenumber);
        //block = request.get("btnRadio").asText();System.out.println(block);
        address = Register.checkAddress(request.get("btnRadio").asText(),request.get("txtRoomnumber").asText());System.out.println(address);
        List<String> errorList = new ArrayList<>();
        errorList.add(username);errorList.add(password);errorList.add(email);errorList.add(firstname);
        errorList.add(lastname);errorList.add(date);errorList.add(phonenumber);errorList.add(address);
        for(String message : errorList){
            if(message.isEmpty()){
                continue;
            }else{
                CreateAccountError error = new CreateAccountError(username,password,email,firstname,lastname,date,phonenumber,address);
                //return error;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);

    }

}
