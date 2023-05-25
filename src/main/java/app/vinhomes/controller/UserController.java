package app.vinhomes.controller;


import app.vinhomes.CreateErrorCatcher;
import app.vinhomes.ErrorChecker;
import app.vinhomes.ResponseJoinEntity.JoinAccountInfo;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.Address;
import app.vinhomes.entity.Phone;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.PhoneRepository;
import app.vinhomes.services.AccountService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(value = "/UserRestController")
public class UserController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private ErrorChecker errorChecker;
    @PostMapping(value = "/createAccountCustomer/{rolenumber}",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateErrorCatcher> createAccountForCustomer(@RequestBody JsonNode request,@PathVariable int rolenumber) {
        System.out.println("inside create account");
        System.out.println(request.asText());
        String username, password, email, firstname, lastname, phonenumber, date, address;
        username = errorChecker.checkUsername(request.get("txtUsername").asText());
        password = errorChecker.checkPassword(request.get("txtPassword").asText());
        email = errorChecker.checkEmail(request.get("txtEmail").asText());
        firstname = errorChecker.checkFirstname(request.get("txtFirstname").asText());
        lastname = errorChecker.checkLastname(request.get("txtLastname").asText());
        date = errorChecker.checkDate(request.get("txtDate").asText());
        System.out.println(request.get("txtDate").asText());
        phonenumber = errorChecker.checkPhoneNumber(request.get("txtPhonenumber").asText());
        address = errorChecker.checkAddress(request.get("btnRadio").asText(), request.get("txtRoomnumber").asText());
        //this is where we change direction if worker or customer account
        //
        //role = 1 is worker
        List<String> errorList = new ArrayList<>();
        errorList.add(username);
        errorList.add(password);
        errorList.add(email);
        errorList.add(firstname);
        errorList.add(lastname);
        errorList.add(date);
        errorList.add(phonenumber);
        errorList.add(address);
        CreateErrorCatcher error = new CreateErrorCatcher(username, password, email, firstname, lastname, date, phonenumber, address);
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //convert String to LocalDate
            LocalDate localDate = LocalDate.parse(request.get("txtDate").asText(), formatter);
            Phone phone = Phone.builder().number(request.get("txtPhonenumber").asText()).build();
            Address address1 = Address.builder().buildingBlock(request.get("btnRadio").asText()).buildingRoom(request.get("txtRoomnumber").asText()).build();
            Account account = Account.builder()
                    .accountName(request.get("txtUsername").asText())
                    .password(request.get("txtPassword").asText())
                    .email(request.get("txtEmail").asText())
                    .firstName(request.get("txtFirstname").asText())
                    .lastName(request.get("txtLastname").asText())
                    .dob(localDate)
                    .role(rolenumber)
                    .accountStatus(1)
                    .address(address1)
                    .build();
            accountRepository.save(account);
            phone.setAccount(account);
            phoneRepository.save(phone);
        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }catch (Exception e){
            System.out.println("something wrong with saving the account");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
        return ResponseEntity.status(HttpStatus.OK).body(error);

    }
    @PostMapping(value = "/createAccountWorker/{rolenumber}",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateErrorCatcher> createAccountForWorker(@RequestBody JsonNode request,@PathVariable int rolenumber){
        System.out.println("inside create account for worker");
        System.out.println(request.asText());
        String username, password, email, firstname, lastname, phonenumber, date, address;
        username = errorChecker.checkUsername(request.get("txtUsername").asText());
        password = errorChecker.checkPassword(request.get("txtPassword").asText());
        email = errorChecker.checkEmail(request.get("txtEmail").asText());
        firstname = errorChecker.checkFirstname(request.get("txtFirstname").asText());
        lastname = errorChecker.checkLastname(request.get("txtLastname").asText());
        date = errorChecker.checkDate(request.get("txtDate").asText());
        System.out.println(request.get("txtDate").asText());
        phonenumber = errorChecker.checkPhoneNumber(request.get("txtPhonenumber").asText());
        List<String> errorList = new ArrayList<>();
        errorList.add(username);
        errorList.add(password);
        errorList.add(email);
        errorList.add(firstname);
        errorList.add(lastname);
        errorList.add(date);
        errorList.add(phonenumber);
        CreateErrorCatcher error = new CreateErrorCatcher(username, password, email, firstname, lastname, date, phonenumber, "");
        for (String message : errorList) {
            if (message.isEmpty()) {
                continue;
            } else {
                //return error;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            //convert String to LocalDate
            LocalDate localDate = LocalDate.parse(request.get("txtDate").asText(), formatter);
            Phone phone = Phone.builder().number(request.get("txtPhonenumber").asText()).build();
            Account account = Account.builder()
                    .accountName(request.get("txtUsername").asText())
                    .password(request.get("txtPassword").asText())
                    .email(request.get("txtEmail").asText())
                    .firstName(request.get("txtFirstname").asText())
                    .lastName(request.get("txtLastname").asText())
                    .dob(localDate)
                    .role(rolenumber)
                    .accountStatus(1)
                    .build();
            accountRepository.save(account);
            phone.setAccount(account);
            phoneRepository.save(phone);
        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }catch (Exception e){
            System.out.println("something wrong with saving the account");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
        return ResponseEntity.status(HttpStatus.OK).body(error);
    }
    @GetMapping(value = "/getAccountWorker",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getAllWorker(){
        int workerRoleValue = 1;
        List<Account> workerList = new ArrayList<>();
        workerList = accountRepository.findByRoleEquals(workerRoleValue);
        return workerList ;
    }
    @GetMapping(value ="/getAccountCustomer", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getAllCustomer(){
        int CustomerRoleValue = 0;
        List<Account> CustomerList = new ArrayList<>();
        CustomerList = accountRepository.findByRoleEquals(CustomerRoleValue);
        return CustomerList ;
    }


    @DeleteMapping(value = "/{ID}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> deleteByIDAndReturn(@PathVariable long ID){
        System.out.println("call DELETE success, id is: "+ ID );
        ResponseEntity<List<Account>> response = accountService.deleteByID(ID);
        return response ;
    }

    @PutMapping(value = "/updateWorkerAccount",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> updateByIdAndReturnWorker(@RequestBody JsonNode request){
        System.out.println("yes call update");
        ResponseEntity<List<Account>> response = accountService.updateAccountById(request);
        return response;
    }
    @GetMapping(value = "/testing/{accountID}",produces = MediaType.APPLICATION_JSON_VALUE)
    public JoinAccountInfo testingGetJoinCustomer(@PathVariable String accountID){
        System.out.println("call success");
        JoinAccountInfo accountInfo = accountService.getCustomerWithPhoneAndAddress(Long.parseLong(accountID));
        return accountInfo;
    }
    @GetMapping(value = "/getAccountInfo/{ID}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Account getAccountById(@PathVariable String ID){
        Account account = accountRepository.findById(Long.parseLong(ID)).get();
        return account;
    }
    @PostMapping (value = "/getAccountPhonenumber",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Phone> getPhonenumberById(@RequestBody Account account ){
        List<Phone> phoneList = phoneRepository.findByAccount(account);
        return phoneList;
    }
}
