package app.vinhomes.service;

import app.vinhomes.common.ErrorChecker;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.customer.AddressRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@org.springframework.stereotype.Service
public class AccountService {

    @Autowired
    private ErrorChecker errorChecker;
    @Autowired
    private AccountRepository accountRepository ;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private WorkerStatusRepository workerStatusRepository;

    public ResponseEntity<List<Account>> updateAccountById(JsonNode request){
        ErrorChecker errorChecker = new ErrorChecker();
        String userid,username,password,  email, firstname, lastname,dob, status,role;
        userid = request.get("txtID").asText();
        username = request.get("txtUsername").asText();
        password = request.get("txtPassword").asText();
        email = request.get("txtEmail").asText();
        firstname =  request.get("txtFirstname").asText();
        lastname = request.get("txtLastname").asText();
        dob =  request.get("txtDob").asText();
        status =  request.get("txtStatus").asText();
        role =  request.get("txtRole").asText();
        System.out.println(userid +"  "+username +"  "+password +"  "+email +"  "+firstname +"  "+lastname +"  "+dob +"  "+status +"  "+role);
        List<String> errorList = new ArrayList<>();
        List<Account> workerList = new ArrayList<>();
        errorList.add(errorChecker.checkFirstname(firstname));
        errorList.add(errorChecker.checkLastname(lastname));
        errorList.add(errorChecker.checkStatus(status));
        System.out.println("inside service account");
        for(String message : errorList){
            if(message.isEmpty()){
                continue;
            }else{
                //return error;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }
        Account accountWorker = accountRepository.findById(Long.parseLong(userid)).get();
        accountWorker.setFirstName(firstname);
        accountWorker.setLastName(lastname);
        accountWorker.setAccountStatus(Integer.parseInt(status));
        accountRepository.save(accountWorker);
        int workerole = 1;
        workerList = accountRepository.findByRoleEquals(workerole);
        return ResponseEntity.status(HttpStatus.OK).body(workerList);
    }
    public ResponseEntity<List<Account>> deleteByID (long id){
        try{
            Account account = accountRepository.findById(id).get();
            account.setAccountStatus(0);
            WorkerStatus getWorkerStatus = account.getWorkerStatus();
            getWorkerStatus.setStatus(1);
            accountRepository.save(account);
            workerStatusRepository.save(getWorkerStatus);
            List<Account> workerList = new ArrayList<>();
            int role = account.getRole();
            workerList =  accountRepository.findByRoleEquals(role);
            return ResponseEntity.status(HttpStatus.OK).body(workerList);
        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }


    }
    public boolean checkEmail(String email){
        List<Account> listaccount = accountRepository.findAll();
        for(Account acc : listaccount){
            if(acc.getEmail() != null && acc.getEmail().equals(email)){
                return true;
            }
        }
        return false;

    }
    public Account getCurrentlyLogginAccount(Authentication authentication){
        System.out.println("inside getCurrentlyLogginAccount");
        Account account = null;
        String username = null;
        try{
            Object principle = authentication.getPrincipal();
            if (principle instanceof UserDetails) {
                username = ( (UserDetails) principle). getUsername();
                account = accountRepository.findByAccountName(username);
                return account;
            } else {
                username = principle.toString();
                return null;
            }
        }catch (Exception e){
            return null;
        }
    }

    public Account getAccountByUsername(String username)throws NullPointerException{
        return accountRepository.findUsername(username).get();
    }
    public void setEnableToAccount(Account account){
        account.setIsEnable(1);
        accountRepository.save(account);
        System.out.println("account is enabled through sms, you can get order now");
    }
    public String changePassword_ForgetAccountService(Account account){
        try{
            String generatedPassword = UUID.randomUUID().toString().substring(0,20);/// can be replaced with some random generated password
            account.setPassword(passwordEncoder.encode(generatedPassword));
            accountRepository.save(account);
            return generatedPassword;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public String changePassword(Long accountId,String oldPassword,String newPassword){
        try{
            Account getAccount = accountRepository.findByAccountId(accountId);
            if(getAccount != null){
                String getPassword = getAccount.getPassword();
                if(passwordEncoder.matches(oldPassword,getPassword)){
                    System.out.println("old password matches current password in database");
                    if(oldPassword.equals(newPassword)){
                        System.out.println("ERROR new password is the same as the old one");
                        return "ERROR new password is the same as the old one";
                    }
                    String getError = errorChecker.checkPassword(newPassword);
                    System.out.println(getError);
                    if(getError.equals("") == false){
                        return "ERROR "+getError;
                    }else{
                        System.out.println("all condition are met, your new pass is: "+newPassword);
                        getAccount.setPassword(passwordEncoder.encode(newPassword));
                        accountRepository.save(getAccount);
                        return "";
                    }
                }else{
                    System.out.println("ERROR old password DO NOT matches current password in database");
                    return "ERROR old password dont match yo input password";
                }
            }else{
                return "ERROR cannot found your account, try re-login";
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return "ERROR server error: ";
        }
    }
    public Account getAccountById(Long id){
        Optional<Account> getAccount = accountRepository.findById(id);
        if(getAccount.isPresent()){
            return getAccount.get();
        }else{
            return null;
        }
    }
}
