package app.vinhomes.controller;

import app.vinhomes.event.event_storage.SendEmailForgetAccount;
import app.vinhomes.event.event_storage.SendEmailOnCreateAccount;
import app.vinhomes.event.event_storage.SendSmsForgetAccount;
import app.vinhomes.event.event_storage.SendSmsOnCreateAccount;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.customer.AddressRepository;
import app.vinhomes.security.authentication.AuthenticationService;
import app.vinhomes.common.CreateErrorCatcher;
import app.vinhomes.common.ErrorChecker;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.repository.customer.PhoneRepository;

import app.vinhomes.security.esms.otp_service.OTPService;
import app.vinhomes.service.AccountService;
import app.vinhomes.service.PhoneService;
import com.azure.core.annotation.Get;
import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/createAccountAPI")
public class AccountAPI {

    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private ErrorChecker errorChecker;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private OTPService otpService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PhoneService phoneService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;//CreateErrorCatcher
    @Autowired
    private AddressRepository addressRepository;

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
                    .isBlock(false)
                    .imgProfileExtension("")
                    .build();
            addr.setAccount(acc);
            acc.setAddress(addr);
            Account response = authenticationService.register(acc);
            System.out.println("save account");

            //addressRepository.save(addr);
            phone.setAccount(acc);
            phoneRepository.save(phone);
            System.out.println("save phone");
            //////////send verification
            //emailService.sendSimpleVerficationEmail(response);
            //////////send verification
            System.out.println("okk, SAVE SUCCESS");

            return ResponseEntity.status(HttpStatus.OK).body(response.getAccountId().toString());
        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.out.println("something wrong with saving the account");
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping(value = "/verificationMethod/{username}/{method}")
    public ResponseEntity<String> chooseVerficationMethod(@PathVariable String method, @PathVariable String username) {
        try {
            System.out.println("inside verification method");
            Account account = accountService.getAccountByUsername(username);
            System.out.println(account);
            if (account == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("cannot found account");
            }
            if (method.equals("EMAIL")) {
                eventPublisher.publishEvent(new SendEmailOnCreateAccount(account));
                System.out.println("already send email");
                return ResponseEntity.ok().body("yes send verification success, check yo mail");
            } else if (method.equals("SMS")) {
                eventPublisher.publishEvent(new SendSmsOnCreateAccount(account));
                System.out.println("already send sms");
                return ResponseEntity.ok().body("yes send verification success, check yo phone, timeout=" + otpService.getOTP_TIMEOUT_SECOND());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("unknown verification method");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server Error, try again later");
        }
    }

    @PostMapping(value = "/forgetAccountMethod", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> chooseVerficationMethod_forgetAccount(@RequestBody JsonNode jsonNode) {
        try {
            String getMethod = jsonNode.get("method").asText().trim();
            String getInput = jsonNode.get("input").asText().trim();
            System.out.println("inside forget password api: " + getMethod + " " + getInput);
            if (getMethod.equals("EMAIL")) {
                String getError = errorChecker.checkForgotPassword_email(getInput);
                System.out.println(getError);
                if (getError != null && getError.equals("")) {
                    eventPublisher.publishEvent(new SendEmailForgetAccount(getInput));
                    return ResponseEntity.status(HttpStatus.OK).body("YES success, check yo mail");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getError);
                }
            } else if (getMethod.equals("SMS")) {
                String getError = errorChecker.CheckForgotPassword_sms(getInput);
                System.out.println(getError);
                if (getError != null && getError.equals("")) {
                    eventPublisher.publishEvent(new SendSmsForgetAccount(getInput));
                    return ResponseEntity.status(HttpStatus.OK).body("YES success, check yo phone");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getError);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR invalid method, try again");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("SERVER ERROR, TRY AGAIN LATER");
        }
    }

    @GetMapping(value = "/changePassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> changePassword(@RequestBody JsonNode jsonNode) {
        try {
            String getOldPassword = jsonNode.get("oldpassword").asText().trim();
            String getNewPassword = jsonNode.get("newpassword").asText().trim();
            String getAccountId = jsonNode.get("accountid").asText().trim();
            Long parsedAccountId = Long.parseLong(getAccountId);
            String getResult = accountService.changePassword(parsedAccountId, getOldPassword, getNewPassword);
            if (getResult.equals("") == false) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getResult);
            } else {
                return ResponseEntity.status(HttpStatus.OK).body("Yes Success");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR yo account id have some problem, try re-login");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR server error try again later");
        }
    }

    @GetMapping(value = "/getAllPhonenumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Phone>> getAllPhonenumberByAccount(HttpServletRequest request) {
        try {
            if (request.getSession(false) == null) {
                List<Phone> emptyList = new ArrayList<>();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emptyList);
            } else {
                Account account = (Account) request.getSession().getAttribute("loginedUser");
                return ResponseEntity.status(HttpStatus.OK).body(phoneService.getAllPhonenumberByAccountId(account.getAccountId()));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            List<Phone> emptyList = new ArrayList<>();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList);
        }
    }
}

