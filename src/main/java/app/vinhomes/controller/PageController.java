package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.repository.order.ServiceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class PageController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(HttpServletRequest request, Model model, HttpServletResponse response) {
        System.out.println("in page controller");
        Account acc = getSessionAccount(request);
        System.out.println("context: "+ SecurityContextHolder.getContext().getAuthentication());
        if(acc != null){
            System.out.println(acc.getAccountName());
            model.addAttribute("acc", acc);
            System.out.println(model.getAttribute("acc"));
            if (acc.getRole() == 2) {
                return "adminShow";
            }
            else if (acc.getRole() == 1) {
                return "staff";
            }
        }

        return "homepage";
    }
//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public String home(HttpServletRequest request,  HttpServletResponse response) {
//        System.out.println("in page controller");
//        String authority = (String) request.getSession().getAttribute("Authority");
//        System.out.println("context: "+ authority);
//        if(authority != null){
//            if (authority.equals("2")) {
//                return "adminShow";
//            }
//            else if (authority.equals("1")) {
//                return "staff";
//            }
//        }
//
//        return "homepage";
//    }
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin() {
        return "login";
    }
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAdmin() {
        System.out.println("inside call admin");
        return "adminShow";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegister() {

        return "register";
    }
    @RequestMapping(value = "/worker")
    public String getWorker(){
        System.out.println("inside call staff");
        return "staff";
    }
    @RequestMapping(value = "/service/{id}", method = RequestMethod.GET)
    public String prepareOrder (@PathVariable("id") Long serviceId, Model model, HttpServletRequest request) {
        if(request.getSession() != null){
            HttpSession session = request.getSession(false);
            Account acc = (Account)  session.getAttribute("loginedUser");
            model.addAttribute("acc", acc);
        }
        String cateName = request.getParameter("cname");
        model.addAttribute("cateName", cateName);
        model.addAttribute("service", serviceRepository.getServicesByServiceId(serviceId));
        model.addAttribute("ordersByService", orderRepository.findAllByService_ServiceId(serviceId));
        return "serviceDetail";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getProfile(HttpServletRequest request, Model model){
        Account acc = getSessionAccount(request);
        if(acc == null){
            return "login";
        }
        model.addAttribute("acc", acc);
        List<String> p = phoneRepository.getPhoneNumberById(acc.getAccountId().intValue());
        System.out.println(p);
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

    @RequestMapping(value = "/yourOrders")
    public String yourOrder(){
        return "viewOrder";
    }

    @RequestMapping(value = "/detail")
    public String detail() { return "Detail"; }


}
