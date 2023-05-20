package app.vinhomes.controller;


import app.vinhomes.Register.Register;
import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/UserController")
public class UserController {
//    @GetMapping(path="/")
//    public String index(){
//        return "index.html";
//
//    }
    @Autowired
    private AccountRepository account;
    @GetMapping(path = "/createAccount")
    public ModelAndView createAccount(HttpServletRequest request ){
        String username, password, email, firstname, lastname, date;
        username = request.getParameter("txtUsername");
        password = request.getParameter("txtPassword");
        email = request.getParameter("txtEmail");
        firstname = request.getParameter("txtFirstname");
        lastname = request.getParameter("txtLastname");
        date = request.getParameter("txtDate");

            System.out.println(username + password + email + firstname + lastname + date);
            List<Account> list = account.findAll();
        ModelAndView modelAndView = new ModelAndView();
        for (Account acc : list) {
            if  (acc.getAccountName().equals("username")) {
                System.out.println("this username is registered before");

                modelAndView.setViewName("homepage.html");
                return modelAndView;
            }
        }
        modelAndView.setViewName("index.html");
        return new ModelAndView("redirect:/index.html");
    }
}
