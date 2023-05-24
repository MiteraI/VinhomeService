package app.vinhomes.controller;


import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
public class AdminController {
    @Autowired
    private AccountRepository accountRepository;

//    @GetMapping(path = "/getAccount" , produces = MediaType.APPLICATION_JSON_VALUE)
//    public List<Account> getAllAccount(){
//        return accountRepository.findAll() ;
//    }

//    @PostMapping(path = "/insertAccount", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public String insertAccount(@RequestBody Account account){
//        try{
//            accountRepository.save(account);
//            return "insert success";
//        }
//        catch(Exception e){
//            System.out.println("insert fail, something wrong, "+ e);
//        }
//        return "insert ERROR!!";
//
//    }

//    @PostMapping(path = "/form", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public String getFormData(@RequestBody JsonNode newAccount){
//        System.out.println(newAccount.toString());
//        try{
//            //JSONObject obj = new JSONObject(newAccount);
//            System.out.println(newAccount.get("txtUsername").asText());
//            System.out.println( newAccount.get("txtPassword").asText());
//            System.out.println( newAccount.get("isAdmin").asText());
//        }catch(Exception e){
//            System.out.println("something wrong with login");
//            return "something wrong with login info";
//        }
//
//
//        return "yes, receive info, extraction is successful";
//    }
}
