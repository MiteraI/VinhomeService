package app.vinhomes.controller;


import app.vinhomes.Register.CreateErrorCatcher;
import app.vinhomes.Register.ErrorChecker;
import app.vinhomes.entity.Account;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        //address = errorChecker.checkAddress(request.get("btnRadio").asText(), request.get("txtRoomnumber").asText());
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
        errorList.add("");
        CreateErrorCatcher error = new CreateErrorCatcher(username, password, email, firstname, lastname, date, phonenumber, "");
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
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date datetoadd = new Date();
            datetoadd = simpleDateFormat.parse(request.get("txtDate").asText());
            Phone phone = Phone.builder().number(phonenumber).build();
            Account account = Account.builder()
                    .accountName(request.get("txtUsername").asText())
                    .password(request.get("txtPassword").asText())
                    .email(request.get("txtEmail").asText())
                    .firstName(request.get("txtFirstname").asText())
                    .lastName(request.get("txtLastname").asText())
                    .dob(datetoadd)
                    .role(rolenumber)
                    .accountStatus(1)
                    .build();
            accountRepository.save(account);
            phone.setAccount(account);
            phoneRepository.save(phone);
        } catch (ParseException e) {
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
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            Date datetoadd = new Date();
            datetoadd = simpleDateFormat.parse(request.get("txtDate").asText());
            Phone phone = Phone.builder().number(phonenumber).build();
            Account account = Account.builder()
                    .accountName(request.get("txtUsername").asText())
                    .password(request.get("txtPassword").asText())
                    .email(request.get("txtEmail").asText())
                    .firstName(request.get("txtFirstname").asText())
                    .lastName(request.get("txtLastname").asText())
                    .dob(datetoadd)
                    .role(rolenumber)
                    .accountStatus(1)
                    .build();
            accountRepository.save(account);
            phone.setAccount(account);
            phoneRepository.save(phone);
        } catch (ParseException e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }catch (Exception e){
            System.out.println("something wrong with saving the account");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
        return ResponseEntity.status(HttpStatus.OK).body(error);
    }
    @GetMapping(value = "/getAccount",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getAllWorker(){
        int workerRoleValue = 1;
        List<Account> workerList = new ArrayList<>();
        workerList = accountRepository.findByRoleEquals(workerRoleValue);
        return workerList ;
    }
    @DeleteMapping(value = "/{workerID}",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> deleteByIDAndReturnWorker(@PathVariable long workerID){
        System.out.println("call DELETE success, id is: "+ workerID );
        ResponseEntity<List<Account>> response = accountService.deleteWorkerById(workerID);
        return response ;
    }

    @PutMapping(value = "/updateWorkerAccount",consumes = MediaType.APPLICATION_JSON_VALUE,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> updateByIdAndReturnWorker(@RequestBody JsonNode request){
        System.out.println("yes call update");
        ResponseEntity<List<Account>> response = accountService.updateAccountById(request);
        return response;
    }
}
