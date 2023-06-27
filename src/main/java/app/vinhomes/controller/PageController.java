package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.repository.order.ServiceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;


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
    public String home(HttpServletRequest request, Model model) {
        //System.out.println("1");
        Account acc = getSessionAccount(request);
        if (acc != null) {
            model.addAttribute("acc", acc);
            if (acc.getRole() == 2) {
                return "AdminShow";
            }
            else if (acc.getRole() == 1) {
                return "scheduleTable";
            }
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
    public String prepareOrder(@PathVariable("id") Long serviceId, Model model, HttpServletRequest request) {
        if (request.getSession() != null) {
            HttpSession session = request.getSession(false);
            Account acc = (Account) session.getAttribute("loginedUser");
            model.addAttribute("acc", acc);
        }
        String cateName = request.getParameter("cname");
        model.addAttribute("cateName", cateName);
        model.addAttribute("service", serviceRepository.getServicesByServiceId(serviceId));
        model.addAttribute("ordersByService", orderRepository.findAllByService_ServiceId(serviceId));
        return "serviceDetail";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getProfile(HttpServletRequest request, Model model) {
        Account acc = getSessionAccount(request);
        Address address = getUserAddress(request);
        List<Phone> phoneNumber = getUserFone(request);
        if (acc == null) {
            return "redirect:/login";
        }
        System.out.println(phoneNumber);
        model.addAttribute("acc", acc);
        model.addAttribute("address", address);
        model.addAttribute("phone", phoneNumber);
        return "profile";
    }

    @RequestMapping(value = "/resetpwd", method = RequestMethod.GET)
    public String resetPassword(HttpServletRequest request, Model model) {
        Account acc = getSessionAccount(request);
        if (acc == null) {
            return "redirect:/login";
        }
        return "resetpassword";
    }

    @RequestMapping(value = "/api/admin/order/user", method = RequestMethod.GET)
    public String getUserOrders(@RequestParam(value = "id") Long uid, Model model) {
        Account acc = accountRepository.findById(uid).get();
        model.addAttribute("orderDetail", orderRepository.findAllByAccount(acc));
        System.out.println(orderRepository.findAllByAccount(acc));
        return "userOrderHistory";
    }

    public void updateSessionAccount(HttpServletRequest request, Account account) {
        if (request.getSession(false) != null) {
            request.getSession().setAttribute("loginedUser", account);
        }
    }

    public void updateSessionAccount(HttpServletRequest request) throws Exception, NullPointerException {
        Account acc = null;
        if (request.getSession(false) != null) {
            HttpSession session = request.getSession(false);
            if (session.getAttribute("loginedUser") != null) {
                acc = (Account) session.getAttribute("loginedUser");
                acc = accountRepository.findById(acc.getAccountId()).get();
                session.setAttribute("loginedUser",acc);
            }
        }
    }

    public Account getSessionAccount(HttpServletRequest request) {
        //System.out.println("2");
        Account acc = null;
        if (request.getSession(false) != null) {
            HttpSession session = request.getSession(false);
            System.out.println(session.getId());
            if (session.getAttribute("loginedUser") != null) {
                System.out.println("PC.getSessionAccount: " + session.getAttribute("loginedUser"));
                acc = (Account) session.getAttribute("loginedUser");
            }
        }
        return acc;
    }

    public Address getUserAddress(HttpServletRequest request) {
        Address addr = null;
        if (request.getSession(false) != null) {
            HttpSession session = request.getSession(false);
            System.out.println("address session not null");
            if (session.getAttribute("address") != null) {
                addr = (Address) session.getAttribute("address");
            }
        }
        return addr;
    }

    public List<Phone> getUserFone(HttpServletRequest request) {
        List<Phone> fone = new ArrayList<>();
        if (request.getSession(false) != null) {
            HttpSession session = request.getSession(false);
            System.out.println("phone's session not null");
            if (session.getAttribute("phone") != null) {
                fone = (ArrayList<Phone>) session.getAttribute("phone");
                System.out.println("fone's size: " + fone.size());

            }
        }
        return fone;
    }

    @RequestMapping(value = "/yourOrders")
    public String yourOrder() {
        return "viewOrder";
    }

    @RequestMapping(value = "/detail")
    public String detail(@RequestParam Optional<Long> orderId, @RequestParam Optional<Long> userId, HttpServletRequest request, Model model) {
        Account acc = getSessionAccount(request);
        Order order = null;
        String url = "";
        if (acc == null) {
            return "redirect:/login";
        }
        if (userId.isPresent()) {
            Account userAcc = accountRepository.findById(userId.get()).get();
            List<Order> orderList = orderRepository.findAllByAccount(userAcc);
            for (Order o : orderList) {
                if (o.getOrderId().equals(orderId.get())) {
                    order = o;
                    System.out.println(order);
                    break;
                }
            }
        }
        switch (acc.getRole()) {
            case 0:
                url = "Detail";
                break;
            case 2:
                model.addAttribute("orderDetail", order);
                url = "userOrderDetail";
                break;
            default:
                url = "staff";
                System.out.println("slave are not allowed to see customer orders");
                break;
        }
        return url;
    }

    @RequestMapping(value = "/homepage")
    public String homepage() {
        return "homepage";
    }

    @RequestMapping(value = "/vnpay", method = RequestMethod.GET)
    public String vnpay() {
        return "vnpay";
    }

    @RequestMapping(value = "/vnpayreturn", method = RequestMethod.GET)
    public String vnpayreturn() {
        return "vnpayreturn";
    }

    @RequestMapping(value = "/transactionReturn", method = RequestMethod.GET)
    public String transactionReturn() {
        return "transactionReturn";
    }

    @RequestMapping(value = "/verification/{username}", method = RequestMethod.GET)
    public String verification(@PathVariable String username) {
        return "redirect:/verificationMethod?username=" + username;
    }

    @RequestMapping(value = "/verificationMethod")
    public String verificationMethodReturn() {
        return "verificationMethod";
    }

    @RequestMapping(value = "/TESTBED", method = RequestMethod.GET)
    public String TESTBED() {
        return "TESTBED";
    }
}
