package app.vinhomes.controller;


import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller //Use like servlet with link not in web.xml
public class LoginFormController {
    @Autowired
    private AccountRepository accountRepository;
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginforfun(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Account loginedUser = accountRepository.findByAccountNameAndPassword(request.getParameter("accountName"), request.getParameter("password"));
        session.setAttribute("loginedUser", loginedUser );
        System.out.println(session.getAttribute("loginedUser"));
        return "homepage";
    }
}