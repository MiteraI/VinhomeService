package app.vinhomes.controller;

import app.vinhomes.common.CreateErrorCatcher;
import app.vinhomes.common.ErrorChecker;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.*;
import app.vinhomes.repository.customer.AddressRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import app.vinhomes.service.AccountService;
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
import java.util.Optional;

@RestController
@RequestMapping(value = "/UserRestController")
public class AdminAPI {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private ErrorChecker errorChecker;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private WorkerStatusRepository workerStatusRepository;
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @PostMapping(value = "/createAccountWorker/{rolenumber}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateErrorCatcher> createAccountForWorker(@RequestBody JsonNode request,
                                                                     @PathVariable int rolenumber) {
        System.out.println("inside create account for worker");
        System.out.println(request.asText());
        String username, password, email, firstname, lastname, phonenumber, date, address;
        username = errorChecker.checkUsername(request.get("txtUsername").asText().trim());
        System.out.println(username);
        password = errorChecker.checkPassword(request.get("txtPassword").asText().trim());
        System.out.println(password);
        email = errorChecker.checkEmail(request.get("txtEmail").asText().trim());
        System.out.println(email);
        firstname = errorChecker.checkFirstname(request.get("txtFirstname").asText().trim());
        System.out.println(firstname);
        lastname = errorChecker.checkLastname(request.get("txtLastname").asText().trim());
        System.out.println(lastname);
        date = errorChecker.checkDate(request.get("txtDate").asText());
        System.out.println(date);
        System.out.println(request.get("txtDate").asText().trim());
        phonenumber = errorChecker.checkPhoneNumber(request.get("txtPhonenumber").asText().trim());
        System.out.println(phonenumber);
        List<String> errorList = new ArrayList<>();
        errorList.add(username);
        errorList.add(password);
        errorList.add(email);
        errorList.add(firstname);
        errorList.add(lastname);
        errorList.add(date);
        errorList.add(phonenumber);
        CreateErrorCatcher error = new CreateErrorCatcher(username, password, email, firstname, lastname, date,
                phonenumber, "");
        System.out.println("yes pass add error list");
        for (String message : errorList) {
            if (message.isEmpty()) {
                continue;
            } else {
                System.out.println("error input");
                // return error;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // convert String to LocalDate
            LocalDate localDate = LocalDate.parse(request.get("txtDate").asText(), formatter);
            Phone phone = Phone.builder().number(request.get("txtPhonenumber").asText().trim()).build();
            ServiceCategory serviceCategory = serviceCategoryRepository.findById(request.get("txtServiceId").asLong())
                    .get();
            WorkerStatus workerStatus = WorkerStatus.builder().allowedDayOff(10).status(0).workCount(0)
                    .serviceCategory(serviceCategory).build();
            Account account = Account.builder()
                    .accountName(request.get("txtUsername").asText().trim())
                    .password(request.get("txtPassword").asText().trim())
                    .email(request.get("txtEmail").asText().trim())
                    .firstName(request.get("txtFirstname").asText().trim())
                    .lastName(request.get("txtLastname").asText().trim())
                    .dob(localDate)
                    .role(rolenumber)
                    .accountStatus(1)
                    .build();
            accountRepository.save(account);
            System.out.println("save account");
            phone.setAccount(account);
            phoneRepository.save(phone);
            System.out.println("save phone");
            workerStatus.setAccount(account);
            workerStatusRepository.save(workerStatus);
            System.out.println("save worker status");
        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.out.println("something wrong with saving the account");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
        return ResponseEntity.status(HttpStatus.OK).body(error);
    }

    @GetMapping(value = "/getAccountWorker", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getAllWorker() {
        int workerRoleValue = 1;
        List<Account> workerList = new ArrayList<>();
        workerList = accountRepository.findByRoleEquals(workerRoleValue);
        return workerList;
    }

    @GetMapping(value = "/getAccountCustomer", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getAllCustomer() {
        int CustomerRoleValue = 0;
        List<Account> CustomerList = new ArrayList<>();
        CustomerList = accountRepository.findByRoleEquals(CustomerRoleValue);
        return CustomerList;
    }

    @DeleteMapping(value = "/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> deleteByIDAndReturn(@PathVariable long ID) {
        System.out.println("call DELETE success, id is: " + ID);
        ResponseEntity<List<Account>> response = accountService.deleteByID(ID);
        return response;
    }

    @PutMapping(value = "/updateWorkerAccount", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> updateByIdAndReturnWorker(@RequestBody JsonNode request) {
        System.out.println("yes call update");
        ResponseEntity<List<Account>> response = accountService.updateAccountById(request);
        return response;
    }

    @GetMapping(value = "/getAccountInfo/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Account getAccountById(@PathVariable String ID) {
        Account account = accountRepository.findById(Long.parseLong(ID)).get();
        return account;
    }

    @PostMapping(value = "/getAccountPhonenumber", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Phone> getPhonenumberByAccount(@RequestBody Account account) {
        List<Phone> phoneList = phoneRepository.findByAccount(account);
        return phoneList;
    }

    @GetMapping(value = "/getAccountAddress/{accountID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Address getAddressByIdThroughAccount(@PathVariable long accountID) {
        System.out.println("get in address");
        Address address = accountRepository.findById(accountID).get().getAddress();
        return address;
    }

    @PutMapping(value = "/UpdateAccountCustomer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateErrorCatcher> updateAccountCustomer(@RequestBody JsonNode updateInfo) {
        int roleCustomer = 0;
        System.out.println("get in update account customer");
        System.out.println(updateInfo.asText());
        System.out.println("id of account update: " + updateInfo.get("txtID").asText());
        Account account_to_update = accountRepository.findById(updateInfo.get("txtID").asLong()).get();
        String ID, username, password, email, firstname, lastname, phonenumber, date, address;
        Long phoneID;
        if (account_to_update.getAccountName().equals(updateInfo.get("txtUsername").asText().trim())) {
            username = "";
        } else {
            username = errorChecker.checkUsername(updateInfo.get("txtUsername").asText());
        }
        //////////////
        password = errorChecker.checkPassword(updateInfo.get("txtPassword").asText().trim());
        ////////////
        if (account_to_update.getEmail().equals(updateInfo.get("txtEmail").asText().trim())) {
            email = "";
        } else {
            email = errorChecker.checkEmail(updateInfo.get("txtEmail").asText());
        }
        ///////////
        firstname = errorChecker.checkFirstname(updateInfo.get("txtFirstname").asText().trim());
        lastname = errorChecker.checkLastname(updateInfo.get("txtLastname").asText().trim());
        date = errorChecker.checkDate(updateInfo.get("txtDate").asText());
        System.out.println(updateInfo.get("txtDate").asText());
        List<Phone> phoneList = new ArrayList<>();
        phoneList = phoneRepository.findByAccount(account_to_update);// xem laip
        //////////
        System.out.println(updateInfo.get("txtPhonenumber").asText().trim());
        if (updateInfo.get("txtPhoneID").asText().trim().isEmpty() == false) {
            phoneID = Long.parseLong(updateInfo.get("txtPhoneID").asText().trim());
            System.out.println(phoneID);
            phonenumber = phoneRepository.findById(phoneID).get().getNumber();
            if (updateInfo.get("txtPhonenumber").asText().trim().equals(phonenumber)) {
                phonenumber = "";
            } else {
                phonenumber = errorChecker.checkPhoneNumber(updateInfo.get("txtPhonenumber").asText().trim());
            }
        } else {
            phoneID = null;
            phonenumber = errorChecker.checkPhoneNumber(updateInfo.get("txtPhonenumber").asText().trim());
        }

        if (account_to_update.getAddress().getBuildingBlock().equals(updateInfo.get("btnRadio").asText())
                && account_to_update.getAddress().getBuildingRoom()
                .equals(updateInfo.get("txtRoomnumber").asText().trim())) {
            address = "";
        } else {
            address = errorChecker.checkAddress(updateInfo.get("btnRadio").asText(),
                    updateInfo.get("txtRoomnumber").asText().trim());
        }
        // this is where we change direction if worker or customer account
        System.out.println("yes check okkk");
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
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        for (String message : errorList) {
            if (message.isEmpty()) {
                continue;
            } else {
                System.out.println("bad request");
                // return error;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        //
        //
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // convert String to LocalDate
            LocalDate localDate = LocalDate.parse(updateInfo.get("txtDate").asText().trim(), formatter);
            Optional<Phone> phone1 = phoneRepository.findById(phoneID);
            // Phone.builder().number(updateInfo.get("txtPhonenumber").asText().trim()).build();
            Address address1 = addressRepository.findById(account_to_update.getAddress().getAddress_id()).get();System.out.println("👤👤👤👤👤👤");
            address1.setBuildingBlock(updateInfo.get("btnRadio").asText().trim());
            address1.setBuildingRoom(updateInfo.get("txtRoomnumber").asText().trim());
            // Address.builder().buildingBlock(updateInfo.get("btnRadio").asText().trim()).buildingRoom(updateInfo.get("txtRoomnumber").asText()).build();
            System.out.println("yes work");
            addressRepository.save(address1);
            account_to_update.setAccountName(updateInfo.get("txtUsername").asText().trim());
            account_to_update.setPassword(updateInfo.get("txtPassword").asText().trim());
            account_to_update.setEmail(updateInfo.get("txtEmail").asText().trim());
            account_to_update.setFirstName(updateInfo.get("txtFirstname").asText().trim());
            account_to_update.setLastName(updateInfo.get("txtLastname").asText().trim());
            account_to_update.setDob(localDate);
            account_to_update.setAddress(address1);

            accountRepository.save(account_to_update);
            System.out.println("save account");
            if (phone1 != null) {
                phone1.get().setNumber(updateInfo.get("txtPhonenumber").asText());
                phoneRepository.save(phone1.get());
                System.out.println("save phone");
            }
        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.out.println("something wrong with saving the account");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
        System.out.println("okk, SAVE SUCCESS");
        return ResponseEntity.status(HttpStatus.OK).body(error);
    }

    @PostMapping(value = "/getWorkerStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public WorkerStatus getWorkerStatus(@RequestBody Account account) {
        System.out.println("get in address");
        WorkerStatus workerStatus = workerStatusRepository.findByAccount(account);
        return workerStatus;

    }

    @GetMapping(value = "/getServiceCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ServiceCategory> getServiceCate() {
        System.out.println("yes in status");
        List<ServiceCategory> serviceCategoryList = serviceCategoryRepository.findAll();
        return serviceCategoryList;
    }

}
