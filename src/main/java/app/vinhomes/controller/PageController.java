package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.ServiceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class PageController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(HttpServletRequest request, Model model) {
        Account acc = getSessionAccount(request);
        if(acc != null){
            System.out.println(acc.getAccountName());
            model.addAttribute("acc", acc);
            System.out.println(model.getAttribute("acc"));
            if (acc.getRole() == 2) {
                return "admin";
            }
            else if (acc.getRole() == 1) {
                return "staff";
            }
        }
        else{
            System.out.println("null");
        }
        return "homepage";
    }
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin() {
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegister() {
        return "register";
    }

    @RequestMapping(value = "/service/{id}", method = RequestMethod.GET)
    public String getService(@PathVariable("id") String id, HttpServletRequest request, Model model){
        Account acc = getSessionAccount(request);
        System.out.println(id);
        System.out.println("co1");
        model.addAttribute("service", serviceRepository.findById(Long.parseLong(id)).get());
        System.out.println(model.getAttribute("service"));
        if(acc != null) {
            System.out.println(acc.getAccountName());
            model.addAttribute("acc", acc);
        }
        return "service";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getProfile(HttpServletRequest request, Model model){
        Account acc = getSessionAccount(request);
        if(acc == null){
            return "login";
        }
        model.addAttribute("acc", acc);
        System.out.println(acc);
        return "profile";
    }

    public Account getSessionAccount(HttpServletRequest request){
        Account acc = null;
        if(request.getSession(false) != null) {
            HttpSession session = request.getSession(false);
            if(session.getAttribute("loginedUser") != null){
                acc = (Account) session.getAttribute("loginedUser");
            }
        }
        return acc;
    }
    @RequestMapping (value = "/createAccount")
    public String getAccountCreate(){
        return "createAccountCustomer";
    }
    @RequestMapping (value = "/index")
    public String index(){
        return "index.html";
    }
    @RequestMapping(value = "/AdminShow")
    public String admin(){
        return "AdminShow";
    }
    @RequestMapping(value = "/testShow")
    public String test(){
        return "testShow";
    }

}
