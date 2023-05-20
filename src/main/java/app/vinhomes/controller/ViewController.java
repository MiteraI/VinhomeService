package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.print.attribute.standard.Media;
import java.net.http.HttpRequest;

@Controller
@RequestMapping(path = "/homepage")
public class ViewController {
    @PostMapping(path = "/createAccount", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Account createAccount(@RequestBody String newAccount){
        JSONObject account = new JSONObject(newAccount);
        return null;
    }

//    @GetMapping(path = "/")
//    public String getIndexHtml(){
//        return "index.html";
//
//    }
//    @GetMapping(path = "/form" , produces = MediaType.APPLICATION_JSON_VALUE)
//    public String printOutForm(HttpServletRequest request){
//        String isAdmin = request.getParameter("isAdmin");
//        if(isAdmin != null){
//            System.out.println(request.getParameter("txtUsername"));
//            System.out.println(request.getParameter("txtPassword"));
//            return "isAdmin";
//        }
//
//        return "notAdmin";
//    }

}
