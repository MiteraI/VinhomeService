package app.vinhomes.services;

import app.vinhomes.ErrorChecker;

import app.vinhomes.ResponseJoinEntity.JoinAccountInfo;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.Address;
import app.vinhomes.entity.Phone;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.AddressRepository;
import app.vinhomes.repository.PhoneRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository ;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PhoneRepository phoneRepository;

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
            accountRepository.save(account);
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
    public JoinAccountInfo getCustomerWithPhoneAndAddress(long id){
        Account account = accountRepository.findById(id).get();
        List<Phone> phoneList = phoneRepository.findByAccount(account);
        Address address = account.getAddress();
        //List<Address> addressList = new ArrayList<>();
        JoinAccountInfo accountInfo = new JoinAccountInfo(account,phoneList,address);
        return accountInfo;
    }

}
