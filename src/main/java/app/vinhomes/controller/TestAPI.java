package app.vinhomes.controller;

import app.vinhomes.entity.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
        @RequestMapping(value = "/testing")
public class TestAPI {
//    @Autowired
//    private LoginFailRepository loginFailRepository;
//
//    @GetMapping(value = "/attempt")
//    public LoginInfo updateAttempt(){
//        System.out.println("in update");
//        LoginInfo info =         loginFailRepository.getAccountInfoById(59);
//        info.setFailAttempt(1);
//        loginFailRepository.save(info);
//        return loginFailRepository.getAccountInfoById(59);
//
//    }
}
