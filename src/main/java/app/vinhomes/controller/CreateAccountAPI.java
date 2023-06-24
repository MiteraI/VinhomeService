package app.vinhomes.controller;

import app.vinhomes.event.event_storage.SendEmailOnCreateAccount;
import app.vinhomes.event.event_storage.SendSmsOnCreateAccount;
import app.vinhomes.security.authentication.AuthenticationService;
import app.vinhomes.common.CreateErrorCatcher;
import app.vinhomes.common.ErrorChecker;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.repository.customer.PhoneRepository;

import app.vinhomes.security.email.email_service.EmailService;

import app.vinhomes.security.esms.ESMSController;
import app.vinhomes.security.esms.otp_service.ESMSservice;
import app.vinhomes.security.esms.otp_service.OTPService;
import app.vinhomes.service.AccountService;
import com.azure.core.annotation.Get;
import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/createAccountAPI")
public class CreateAccountAPI {

    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private ErrorChecker errorChecker;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ESMSservice esmsService;
    @Autowired
    private OTPService otpService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;//CreateErrorCatcher
    @PostMapping(value = "/createAccountCustomer/{rolenumber}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccountForCustomer(@RequestBody JsonNode request,
                                                                       @PathVariable int rolenumber, HttpServletRequest request_) {
        System.out.println("inside create account");
        System.out.println(request.asText());
        Account acc = null;
        Address addr = null;
        String username, password, email, firstname, lastname, phonenumber, date, address;
        username = errorChecker.checkUsername(request.get("txtUsername").asText().trim());
        password = errorChecker.checkPassword(request.get("txtPassword").asText().trim());
        email = errorChecker.checkEmail(request.get("txtEmail").asText().trim());
        firstname = errorChecker.checkFirstname(request.get("txtFirstname").asText().trim());
        lastname = errorChecker.checkLastname(request.get("txtLastname").asText().trim());
        date = errorChecker.checkDate(request.get("txtDate").asText().trim());
        System.out.println(request.get("txtDate").asText().trim());
        phonenumber = errorChecker.checkPhoneNumber(request.get("txtPhonenumber").asText().trim());
        address = errorChecker.checkAddress(request.get("btnRadio").asText(),
                request.get("txtRoomnumber").asText().trim());
        // this is where we change direction if worker or customer account
        //
        // role = 1 is worker
        List<String> errorList = new ArrayList<>();
        errorList.add(username);
        errorList.add(password);
        errorList.add(email);
        errorList.add(firstname);
        errorList.add(lastname);
        errorList.add(date);
        errorList.add(phonenumber);
        errorList.add(address);
        CreateErrorCatcher error = new CreateErrorCatcher(username, password, email, firstname, lastname, date,
                phonenumber, address);
        for (String message : errorList) {
            if (message.isEmpty()) {
                continue;
            } else {
                // return error;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        //
        //
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // convert String to LocalDate
            LocalDate localDate = LocalDate.parse(request.get("txtDate").asText(), formatter);
            Phone phone = Phone.builder().number(request.get("txtPhonenumber").asText().trim()).build();
            addr = Address.builder().buildingBlock(request.get("btnRadio").asText().trim())
                    .buildingRoom(request.get("txtRoomnumber").asText().trim()).build();
            acc = Account.builder()
                    .accountName(request.get("txtUsername").asText().trim())
                    .password(request.get("txtPassword").asText().trim())
                    .email(request.get("txtEmail").asText().trim())
                    .firstName(request.get("txtFirstname").asText().trim())
                    .lastName(request.get("txtLastname").asText().trim())
                    .dob(localDate)
                    .role(rolenumber)
                    .accountStatus(1)
                    .address(addr)
                    .build();

            Account response = authenticationService.register(acc);
            System.out.println("save account");

            phone.setAccount(acc);
            phoneRepository.save(phone);System.out.println("save phone");
            //////////send verification
            //emailService.sendSimpleVerficationEmail(response);
            //////////send verification
            System.out.println("okk, SAVE SUCCESS");
            HttpSession session = request_.getSession();
            session.setAttribute("loginedUser", acc);
            session.setAttribute("address", addr);
            System.out.println(session.getAttribute("loginedUser"));

            return ResponseEntity.status(HttpStatus.OK).body(response.getAccountId().toString());
        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.out.println("something wrong with saving the account");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }


    }


    @PostMapping(value = "/verificationMethod/{username}/{method}")
    public ResponseEntity<?> chooseVerficationMethod(@PathVariable String method,@PathVariable String username){
        try{
            Account account = accountService.getAccountByUsername(username);
            if(account == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("asdf");
            }
            if(method.equals("EMAIL")){
                eventPublisher.publishEvent(new SendEmailOnCreateAccount(account));
            }else if(method.equals("SMS")){
                eventPublisher.publishEvent(new SendSmsOnCreateAccount(account));
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("asdf");
            }
            return ResponseEntity.ok().body("asdf");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("asdf");
        }
    }

}