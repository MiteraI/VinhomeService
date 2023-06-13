package app.vinhomes.controller;


import app.vinhomes.common.CreateErrorCatcher;
import app.vinhomes.common.ErrorChecker;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.customer.AddressRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/changeProfile")
public class ProfileController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private ErrorChecker errorChecker;

    @PostMapping(value = "/{userId}")
    public ResponseEntity<CreateErrorCatcher> changeProfile(@RequestBody JsonNode request, @PathVariable("userId") Long userId, HttpServletRequest request_) {
        int roleCustomer = 0;
        System.out.println("get in update account customer");
        System.out.println(request.asText());
        Account acc = accountRepository.findById(userId).get();
        Address addr = addressRepository.findById(acc.getAddress().getAddress_id()).get();
        Phone fone = phoneRepository.findById(Long.parseLong(request.get("txtPhoneId").asText().trim())).get();
        String username, email, firstname, lastname, phonenumber, date, address, block;
        List<String> addressArr = new ArrayList<>();
        List<Phone> phoneList = new ArrayList<>();
        username = request.get("txtUsername") == null ? acc.getAccountName() : request.get("txtUsername").asText().trim();
        email = request.get("txtEmail") == null ? acc.getEmail() : request.get("txtEmail").asText().trim();
        firstname = request.get("txtFirstname") == null ? acc.getFirstName() : request.get("txtFirstname").asText().trim();
        lastname = request.get("txtLastname") == null ? acc.getLastName() : request.get("txtLastname").asText().trim();
        date = request.get("txtDate") == null ? acc.getDob().toString() : request.get("txtDate").asText().trim();
        address = request.get("txtRoom") == null ? addr.getBuildingRoom() : request.get("txtRoom").asText().trim();
        block = request.get("radioBlock") == null ? addr.getBuildingBlock() : request.get("radioBlock").asText();
        phonenumber = request.get("txtPhone") == null ? fone.getNumber() : request.get("txtPhone").asText().trim();
        System.out.println(block);
//        Long phoneID;
        username = acc.getAccountName().equals(username) ? "" : errorChecker.checkUsername(username);
        email = acc.getEmail().equals(email) ? "" : errorChecker.checkEmail(email);
        firstname = errorChecker.checkFirstname(firstname);
        lastname = errorChecker.checkLastname(lastname);
        date = errorChecker.checkDate(date);
        System.out.println(address);
        address = addr.getBuildingRoom().equals(address) ? "" : errorChecker.checkAddress(block, address);
        phonenumber = fone.getNumber().equals(phonenumber) ? "" : errorChecker.checkPhoneNumber(phonenumber);

        System.out.println("yes check okkk");
        // role = 1 is worker
        List<String> errorList = new ArrayList<>();
        errorList.add(username);
        errorList.add(email);
        errorList.add(firstname);
        errorList.add(lastname);
        errorList.add(date);
        errorList.add(address);
        errorList.add(phonenumber);
        CreateErrorCatcher error = new CreateErrorCatcher(username, email, firstname, lastname, date, address, phonenumber);
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        for (String message : errorList) {
            if (message.isEmpty()) {
                continue;
            } else {
                System.out.println("bad request");
                System.out.println(message);
                // return error;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        //
        //
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            username = request.get("txtUsername") == null ? acc.getAccountName() : request.get("txtUsername").asText().trim();
            email = request.get("txtEmail") == null ? acc.getEmail() : request.get("txtEmail").asText().trim();
            firstname = request.get("txtFirstname") == null ? acc.getFirstName() : request.get("txtFirstname").asText().trim();
            lastname = request.get("txtLastname") == null ? acc.getLastName() : request.get("txtLastname").asText().trim();
            date = request.get("txtDate") == null ? acc.getDob().toString() : request.get("txtDate").asText().trim();
            LocalDate localDate = LocalDate.parse(date, formatter);
            address = request.get("txtRoom") == null ? addr.getBuildingRoom() : request.get("txtRoom").asText().trim();
            phonenumber = request.get("txtPhone") == null ? fone.getNumber() : request.get("txtPhone").asText().trim();
            System.out.println(phonenumber);

            System.out.println("yes work");

            addr.setBuildingBlock(block);
            addr.setBuildingRoom(address);
            acc.setAccountName(username);
            acc.setEmail(email);
            acc.setFirstName(firstname);
            acc.setLastName(lastname);
            acc.setDob(localDate);
            fone.setNumber(phonenumber);


            phoneRepository.save(fone);
            addressRepository.save(addr);
            accountRepository.save(acc);
            addressArr.add(address);
            addressArr.add(block);
            phoneList.add(fone);
            System.out.println("save account");

        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
        System.out.println("okk, SAVE SUCCESS");
        if(request_.getSession(false) != null){
            HttpSession session = request_.getSession(false);
            if(session.getAttribute("loginedUser") != null){
                session.setAttribute("loginedUser", acc);
            }
            if(session.getAttribute("address") != null){
                session.setAttribute("address", addr);
            }
            if(session.getAttribute("phone") != null){
                session.setAttribute("phone", phoneList);
            }
        }
        error = new CreateErrorCatcher(username, email, firstname, lastname, date, addressArr, phonenumber);
        return ResponseEntity.status(HttpStatus.OK).body(error);
    }
}
