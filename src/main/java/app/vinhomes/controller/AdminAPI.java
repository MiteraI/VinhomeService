package app.vinhomes.controller;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.security.authentication.AuthenticationService;
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
import app.vinhomes.service.TransactionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Getter
@Setter
class WorkerStatusSummary {
    private Account account;
    private ServiceCategory serviceCategory;
    private Integer status;
    private Integer allowedDayOff;
    private Integer workCount;

    public WorkerStatusSummary(Account account, ServiceCategory serviceCategory, Integer status, Integer allowedDayOff, Integer workCount) {
        this.account = account;
        this.serviceCategory = serviceCategory;
        this.status = status;
        this.allowedDayOff = allowedDayOff;
        this.workCount = workCount;
    }

    // getters and setters
}
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
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping(value = "/createAccountWorker/{rolenumber}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateErrorCatcher> createAccountForWorker(@RequestBody JsonNode request,
                                                                     @PathVariable int rolenumber) {
        System.out.println("inside create account for worker");

        String username, password, email, firstname, lastname, phonenumber, date, address;
        username = errorChecker.checkUsername(request.get("txtUsername").asText().trim());
        password = errorChecker.checkPassword(request.get("txtPassword").asText().trim());
        email = errorChecker.checkEmail(request.get("txtEmail").asText().trim());
        firstname = errorChecker.checkFirstname(request.get("txtFirstname").asText().trim());
        lastname = errorChecker.checkLastname(request.get("txtLastname").asText().trim());
        date = errorChecker.checkDate(request.get("txtDate").asText());
        phonenumber = errorChecker.checkPhoneNumber(request.get("txtPhonenumber").asText().trim());
        System.out.println("pass get error");
        List<String> errorList = new ArrayList<>();
        errorList.add(username);
        errorList.add(password);
        errorList.add(email);
        errorList.add(firstname);
        errorList.add(lastname);
        errorList.add(date);
        errorList.add(phonenumber);
        CreateErrorCatcher error =
                new CreateErrorCatcher
                        (username, password, email, firstname, lastname, date,phonenumber, "");

        for (String message : errorList) {
            if (message.isEmpty()) {
                continue;
            } else {
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
                    .isBlock(false)
                    .build();
            Account response = authenticationService.register(account);
            System.out.println("save account");
            phone.setAccount(account);
            phoneRepository.save(phone);

            workerStatus.setAccount(account);
            workerStatusRepository.save(workerStatus);
            System.out.println("save worker status");
        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.out.println("something wrong with saving the account");
            System.out.println("error" + e);
            e.printStackTrace();
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
        System.out.println(CustomerList);
        return CustomerList;
    }

    @DeleteMapping(value = "/deleteWorker/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkerStatusSummary> deleteWorkerByIDAndReturn(@PathVariable long ID) {
        System.out.println("call DELETE success, id is: " + ID);
        List<WorkerStatus> workerStatusList = new ArrayList<>();
        List<WorkerStatusSummary> workerStatusSummaryList = new ArrayList<>();
        Account getAccount = accountRepository.findByAccountId(ID);
        if (getAccount.getAccountStatus() == 1) {
            ResponseEntity<List<Account>> response = accountService.deleteByID(ID);
        }else{
            getAccount.setAccountStatus(1);
            WorkerStatus getWorkerStatus = getAccount.getWorkerStatus() ;
            getWorkerStatus.setStatus(0);
            accountRepository.save(getAccount);
            workerStatusRepository.save(getWorkerStatus);
        }
        workerStatusList = workerStatusRepository.findAll();
        for(int i = 0; i < workerStatusList.size(); i++){
            WorkerStatusSummary workerStatusSummary =
                    new WorkerStatusSummary(workerStatusList.get(i).getAccount(), workerStatusList.get(i).getServiceCategory(), workerStatusList.get(i).getStatus(), workerStatusList.get(i).getAllowedDayOff(),
                            workerStatusList.get(i).getWorkCount());
            workerStatusSummaryList.add(workerStatusSummary);
        }
        System.out.println(workerStatusSummaryList);
        return workerStatusSummaryList;
    }

    @DeleteMapping(value = "/deleteCustomer/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> deleteCustomerByIDAndReturn(@PathVariable long ID) {
        System.out.println("call DELETE success, id is: " + ID);
        Account getAccount = accountRepository.findByAccountId(ID);
        if (getAccount.getAccountStatus() == 1) {
            getAccount.setAccountStatus(0);
            ResponseEntity<List<Account>> response = accountService.deleteByID(ID);
            accountRepository.save(getAccount);
        }else{
            getAccount.setAccountStatus(1);
            accountRepository.save(getAccount);
        }
        List<Account> getList = accountRepository.findByRoleEquals(getAccount.getRole());
        return ResponseEntity.ok().body(getList);
    }

    @PutMapping(value = "/updateAccountWorker/{accountId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateErrorCatcher> updateByIdAndReturnWorker(@RequestBody JsonNode updateInfo) {
        System.out.println("inside update worker");
        int workerRole = 1;
        System.out.println("updateInfo: " + updateInfo.asText());
        String  username, password, email, firstname, lastname, phonenumber, date;
        String daysoff,workcount,status,serviceId;
        Long phoneID,ID;
        ID = updateInfo.get("txtID").asLong();
        Account account_to_update = accountRepository.findById(ID).get();
        //////////////username
        if (account_to_update.getAccountName().equals(updateInfo.get("txtUsername").asText().trim())) {
            username = "";
        } else {
            username = errorChecker.checkUsername(updateInfo.get("txtUsername").asText());
        }
        //////////////password
        password = "";
        try{
            password = errorChecker.checkPassword(updateInfo.get("txtPassword").asText().trim());
        }catch (NullPointerException e){
            System.out.println("error in get password, just normal: "+e.getMessage());
        }
        ////////////email
        if (account_to_update.getEmail().equals(updateInfo.get("txtEmail").asText().trim())) {
            email = "";
        } else {
            email = errorChecker.checkEmail(updateInfo.get("txtEmail").asText());
        }
        ///////////first,last name,date,phone
        firstname = errorChecker.checkFirstname(updateInfo.get("txtFirstname").asText().trim());
        lastname = errorChecker.checkLastname(updateInfo.get("txtLastname").asText().trim());
        date = errorChecker.checkDate(updateInfo.get("txtDate").asText());
        System.out.println(updateInfo.get("txtDate").asText());
        List<Phone> phoneList = new ArrayList<>();
        phoneList = phoneRepository.findByAccount(account_to_update);// xem laip
        //////////
        System.out.println(updateInfo.get("txtPhonenumber").asText().trim());
        String tryInsertNewPhonenumber = null;
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
            System.out.println("no phone number found");
            if(updateInfo.get("txtPhonenumber").asText().trim().isEmpty() == false){
                System.out.println("try insert phone if id is empty");
                phonenumber = errorChecker.checkPhoneNumber(updateInfo.get("txtPhonenumber").asText().trim());
                if(phonenumber.equals("")){
                    tryInsertNewPhonenumber = updateInfo.get("txtPhonenumber").asText().trim();
                }
            }else{
                phonenumber = "";
            }
        }
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
        CreateErrorCatcher error = new CreateErrorCatcher(username, password, email, firstname, lastname, date,
                phonenumber, "");
        for (String message : errorList) {
            if (message.isEmpty()) {
                continue;
            } else {
                System.out.println("bad request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        daysoff = updateInfo.get("txtDayoff").asText().trim();
        workcount = updateInfo.get("txtWorkcount").asText().trim(); //0 = free, 1 = busy
        status = updateInfo.get("txtStatus").asText().trim();System.out.println(status);
        serviceId = updateInfo.get("txtServiceId").asText().trim();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(updateInfo.get("txtDate").asText().trim(), formatter);
            Phone phone1 = null;
            if(phoneID != null){
                 phone1 = phoneRepository.findById(phoneID).get();
            }else{
                if(tryInsertNewPhonenumber != null){
                    phone1 = Phone.builder()
                            .number(tryInsertNewPhonenumber)
                            .account(account_to_update)
                            .build();
                }
            }
            account_to_update.setAccountName(updateInfo.get("txtUsername").asText().trim());
            //account_to_update.setPassword(updateInfo.get("txtPassword").asText().trim());
            account_to_update.setEmail(updateInfo.get("txtEmail").asText().trim());
            account_to_update.setFirstName(updateInfo.get("txtFirstname").asText().trim());
            account_to_update.setLastName(updateInfo.get("txtLastname").asText().trim());
            account_to_update.setDob(localDate);
            accountRepository.save(account_to_update);
            System.out.println("save account");
            if (phone1 != null) {
                if (tryInsertNewPhonenumber == null) {
                    phone1.setNumber(updateInfo.get("txtPhonenumber").asText());
                }
                phoneRepository.save(phone1);
                System.out.println("save phone, try and create if possible");
            }
            ServiceCategory getServiceCategory = null;
            if(serviceId != null || serviceId.isEmpty() == false){
                 getServiceCategory = serviceCategoryRepository.findByServiceCategoryId(Long.parseLong(serviceId));
            }
            WorkerStatus getWorkerStatus = workerStatusRepository.findByAccount(account_to_update);
            System.out.println(status);
            getWorkerStatus.setStatus(Integer.parseInt(status));
            getWorkerStatus.setAllowedDayOff(Integer.parseInt(daysoff));
            getWorkerStatus.setWorkCount(Integer.parseInt(workcount));
            if(getServiceCategory != null){
                getWorkerStatus.setServiceCategory(getServiceCategory);
            }
            workerStatusRepository.save(getWorkerStatus);
            System.out.println("save worker status");
        } catch (DateTimeException e) {
            System.out.println("cant parse date");
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            System.out.println("something wrong with saving the account");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
        System.out.println("okk, SAVE SUCCESS");
        return ResponseEntity.status(HttpStatus.OK).body(error);
    }

    @GetMapping(value = "/getAccountInfo/{ID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Account getAccountById(@PathVariable long ID) {
        System.out.println("inside get Account info");
        Account account = accountRepository.findById(ID).get();
        return account;
    }

    @GetMapping(value = "/getAccountPhonenumber/{accountID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Phone> getPhonenumberByAccount(@PathVariable long accountID) {
        System.out.println("inside get phone number");
        List<Phone> phoneList = phoneRepository.findByAccountId(accountID);
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
        int isEnabled;
        Boolean isBlock;
        Long phoneID;
        if (account_to_update.getAccountName().equals(updateInfo.get("txtUsername").asText().trim())) {
            username = "";
        } else {
            username = errorChecker.checkUsername(updateInfo.get("txtUsername").asText());
        }
        //////////////
        password = "";
        try{
            password = errorChecker.checkPassword(updateInfo.get("txtPassword").asText().trim());
        }catch (NullPointerException e){
            System.out.println("error in get password, just normal: "+e.getMessage());
        }
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
        for (String message : errorList) {
            if (message.isEmpty()) {
                continue;
            } else {
                System.out.println("bad request");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        }
        isEnabled = updateInfo.get("isEnable").asInt();
        System.out.println("isBlock: " + updateInfo.get("isBlock").asInt());
        if(updateInfo.get("isBlock").asInt()==1){
            isBlock = true;
        }else{
            isBlock = false;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            // convert String to LocalDate
            LocalDate localDate = LocalDate.parse(updateInfo.get("txtDate").asText().trim(), formatter);
            Optional<Phone> phone1 = phoneRepository.findById(phoneID);
            // Phone.builder().number(updateInfo.get("txtPhonenumber").asText().trim()).build();
            Address address1 = addressRepository.findById(account_to_update.getAddress().getAddress_id()).get();
            address1.setBuildingBlock(updateInfo.get("btnRadio").asText().trim());
            address1.setBuildingRoom(updateInfo.get("txtRoomnumber").asText().trim());
            System.out.println("yes work");
            addressRepository.save(address1);

            account_to_update.setAccountName(updateInfo.get("txtUsername").asText().trim());
            //account_to_update.setPassword(updateInfo.get("txtPassword").asText().trim());
            account_to_update.setEmail(updateInfo.get("txtEmail").asText().trim());
            account_to_update.setFirstName(updateInfo.get("txtFirstname").asText().trim());
            account_to_update.setLastName(updateInfo.get("txtLastname").asText().trim());
            account_to_update.setDob(localDate);
            account_to_update.setAddress(address1);
            account_to_update.setIsEnable(isEnabled);
            account_to_update.setIsBlock(isBlock);
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

//    @GetMapping(value = "/getWorkerStatus/{account_id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public WorkerStatus getWorkerStatus(@PathVariable long account_id) {
//        System.out.println("get in address");
//        WorkerStatus workerStatus = workerStatusRepository.findWorkerStatusById(account_id);
//        return workerStatus;
//
//    }
    @GetMapping(value = "/getWorkerStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<WorkerStatusSummary> getWorkerStatus() {
        System.out.println("get in address");
        List<WorkerStatus> workerStatusList = new ArrayList<>();
        List<WorkerStatusSummary> workerStatusSummaryList = new ArrayList<>();
            workerStatusList = workerStatusRepository.findAll();
            for(int i = 0; i < workerStatusList.size(); i++){
                WorkerStatusSummary workerStatusSummary =
                        new WorkerStatusSummary(workerStatusList.get(i).getAccount(), workerStatusList.get(i).getServiceCategory(), workerStatusList.get(i).getStatus(), workerStatusList.get(i).getAllowedDayOff(),
                                workerStatusList.get(i).getWorkCount());
                workerStatusSummaryList.add(workerStatusSummary);
            }
            System.out.println(workerStatusSummaryList);
            return workerStatusSummaryList;
    }
    @GetMapping(value = "/getServiceCategory", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getServiceCate() {
        System.out.println("yes in status");
        List<ServiceCategory> serviceCategoryList = serviceCategoryRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(serviceCategoryList);
    }
    @GetMapping(value = "/getTotalCancelOrder",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Long,Integer>getCustomerWithOrderCancel(){
        return countOrderByCustomer(OrderStatus.CANCEL);
    }
    @GetMapping(value = "/getTotalOrder",produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Long,Integer>getCustomerWithOrderTotal(){
        return countOrderByCustomer(null);
    }
    private Map<Long,Integer> countOrderByCustomer(OrderStatus statusOfOrder){
        int customerRole = 0;
        Map<Long,Integer> returnList = new HashMap<>();
        List<Account> getCustomerList = accountRepository.findByRoleEquals(customerRole);
        getCustomerList.forEach( customer -> {
            List<Order> getOrderByEachCustomer = orderRepository.findAllByAccount(customer);
            int countOrder  = 0;
            if(statusOfOrder == null){
                countOrder = getOrderByEachCustomer.size();
            }else{
                for(Order order : getOrderByEachCustomer){
                    if(order.getStatus().equals(statusOfOrder)){
                        countOrder ++;
                    }
                }
            }
            returnList.put(customer.getAccountId(),countOrder);
        });
        return returnList;
    }
}


