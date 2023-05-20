package app.vinhomes.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class PageController {
    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value = "/")
    public String home() {
        return "forward:/homepage.html";
    }
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin() {
        return "forward:/login.html";
    }

}
