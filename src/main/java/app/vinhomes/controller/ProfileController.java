package app.vinhomes.controller;


import app.vinhomes.common.CreateErrorCatcher;
import app.vinhomes.common.ErrorChecker;
import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/changeProfile")
public class ProfileController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private ErrorChecker errorChecker;
    @PostMapping(value = "/{userId}")
    public ResponseEntity<CreateErrorCatcher> changeProfile(@RequestBody JsonNode request, @PathVariable("userId") Long userId, HttpServletRequest request_) {
        System.out.println("inside create account");
        System.out.println(request.asText());
        HttpSession session = request_.getSession();
        Account account = null;
        System.out.println(userId);
        System.out.println(request.get("txtUsername"));
        String username, password, email, firstname, lastname, phonenumber, date, address;
        username = errorChecker.checkUsername(request.get("txtUsername").asText());
        password = errorChecker.checkPassword(request.get("txtPassword").asText());
        email = errorChecker.checkEmail(request.get("txtEmail").asText());
        firstname = errorChecker.checkFirstname(request.get("txtFirstname").asText());
        lastname = errorChecker.checkLastname(request.get("txtLastname").asText());
//        date = errorChecker.checkDate(request.get("txtDate").asText());
//        System.out.println(request.get("txtDate").asText());
//        phonenumber = errorChecker.checkPhoneNumber(request.get("txtPhonenumber").asText());
//        address = errorChecker.checkAddress(request.get("btnRadio").asText(), request.get("txtRoomnumber").asText());
        //this is where we change direction if worker or customer account
        //
        //role = 1 is worker
        List<String> errorList = new ArrayList<>();
        errorList.add(username);
        errorList.add(password);
        errorList.add(email);
        errorList.add(firstname);
        errorList.add(lastname);
//        errorList.add(date);
//        errorList.add(phonenumber);
//        errorList.add(address);
        CreateErrorCatcher error = new CreateErrorCatcher(username, password, email, firstname, lastname);
        for (String message : errorList) {
            if (message.isEmpty()) {
                continue;
            } else {
                //return error;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        //
        //
        try {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //convert String to LocalDate
//            LocalDate localDate = LocalDate.parse(request.get("txtDate").asText(), formatter);
//            Phone phone = Phone.builder().number(request.get("txtPhonenumber").asText()).build();
//            Address address1 = Address.builder().buildingBlock(request.get("btnRadio").asText()).buildingRoom(request.get("txtRoomnumber").asText()).build();
            account = Account.builder()
                    .accountId(userId)
                    .accountName(request.get("txtUsername").asText())
                    .password(request.get("txtPassword").asText())
                    .email(request.get("txtEmail").asText())
                    .firstName(request.get("txtFirstname").asText())
                    .lastName(request.get("txtLastname").asText())
//                    .dob(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
//                    .role(rolenumber)
//                    .accountStatus(1)
//                    .address(address1)
                    .build();
            accountRepository.save(account);System.out.println("save account");
//            phone.setAccount(account);
//            phoneRepository.save(phone);System.out.println("save phone");
        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }catch (Exception e){
            System.out.println("something wrong with saving the account");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
        System.out.println("okk, SAVE SUCCESS");
        username = request.get("txtUsername").asText();
        password = request.get("txtPassword").asText();
        account = accountRepository.findByAccountNameAndPassword(username,password);
        session.setAttribute("loginedUser", account);
        return ResponseEntity.status(HttpStatus.OK).body(error);

    }
}
