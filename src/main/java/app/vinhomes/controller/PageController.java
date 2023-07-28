package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.customer.AddressRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.repository.order.ServiceCategoryRepository;

import app.vinhomes.repository.order.ServiceRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import app.vinhomes.security.SecurityService;
import app.vinhomes.service.ServiceTypeService;
import app.vinhomes.security.email.email_service.EmailService;

import com.azure.core.annotation.Get;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@Controller
public class PageController {
    @Autowired
    private ServiceTypeService typeService;
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private WorkerStatusRepository workerStatusRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(HttpServletRequest request, Model model) {
        //System.out.println("1");
        Account acc = getSessionAccount(request);
        if (acc != null) {
            model.addAttribute("acc", acc);
            if (acc.getRole() == 2) {
                model.addAttribute("category", serviceCategoryRepository.findAll());
                model.addAttribute("worker", workerStatusRepository.findAll());
                System.out.println(serviceCategoryRepository.findAll());
                System.out.println(workerStatusRepository.findAll());
                return "dashboard";
            } else if (acc.getRole() == 1) {
                return "worker-homepage";
            }
        }
        model.addAttribute("category", serviceCategoryRepository.findAll());
        System.out.println(serviceCategoryRepository.findAll());
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

    @RequestMapping(value = "/category-services/{id}", method = RequestMethod.GET)
    public String getAllServiceOfCategory(@PathVariable("id") Long categoryId, Model model) {
        model.addAttribute("services", serviceCategoryRepository.findById(categoryId).get());
        model.addAttribute("ratingArr", new int[]{5, 4, 3, 2, 1});
        return "categoryservices";
    }

    @RequestMapping(value = "/service/{serviceId}")
    public String prepareOrder(@PathVariable("serviceId") Long serviceId, Model model, HttpServletRequest request) {
        model.addAttribute("service", typeService.getServiceType(serviceId));
        model.addAttribute("category", typeService.getServiceCateByServiceId(serviceId));
        model.addAttribute("ordersByService", orderRepository.findAllByService_ServiceId(serviceId));
        return "service-details";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getProfile(HttpServletRequest request, Model model) {
        Account acc = getSessionAccount(request);
        Address address = getUserAddress(request);
        List<Phone> phoneNumber = getUserFone(request);
        System.out.println(phoneNumber);
        model.addAttribute("acc", acc);
        model.addAttribute("address", address);
        model.addAttribute("phone", phoneNumber);
        return "profile";
    }

    @RequestMapping(value = "/worker-Profile", method = RequestMethod.GET)
    public String getWorkerProfile(HttpServletRequest request, Model model) {
        Account acc = getSessionAccount(request);
        List<Phone> phoneNumber = getUserFone(request);
        System.out.println(phoneNumber);
        model.addAttribute("acc", acc);
        model.addAttribute("phone", phoneNumber);
        return "workerProfile";
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
                session.setAttribute("loginedUser", acc);
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
                ////minh ////fix //////
                acc = (Account) session.getAttribute("loginedUser");
//                acc = accountRepository.findById(acc.getAccountId()).get();
//                session.setAttribute("loginedUser", acc);
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

    @RequestMapping(value = "/verificationMethod", method = RequestMethod.GET)
    public String verificationMethodReturn(@RequestParam String username, HttpServletRequest request) {
        if (request.getSession(false) != null) {
            Account acc = (Account) request.getSession(false).getAttribute("loginedUser");
            if (acc.getAccountName().equals(username)) {
                if (accountRepository.findById(acc.getAccountId()).get().isEnabled()) {
                    return "redirect:/";
                } else {
                    return "verificationMethod";
                }
            } else {
                return "redirect:/login";
            }
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "/forget_Account", method = RequestMethod.GET)
    public String forgetAccount() {
        return "forgetAccount";
    }

    @RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
    public String accessDenied() {
        return "error/accessDenied";
    }

    @RequestMapping(value = "/TESTBED", method = RequestMethod.GET)
    public String TESTBED() {
        return "TESTBED";
    }

    @RequestMapping(value = "/mail/verification")
    public String checkEmailVerification(HttpServletRequest request, Model model) {
        try {
            //lay tu link bam tu mail
            System.out.println("inside email Verification");
            String emailTo = request.getParameter("emailTo");
            String tokenValue = request.getParameter("tokenValue");
            String message = emailService.checkIfTokenValeValid(emailTo, tokenValue);
            if (!message.equals("SUCCESS")) {
                System.out.println(message);
                model.addAttribute("statusMessage", "Invalid");
                model.addAttribute("message", "this link have expired, please try again.");
                model.addAttribute("status", HttpStatus.BAD_REQUEST);
            } else {
                //TODO : do something after check mail succeses
                System.out.println(message);
                updateSessionAccount(request);
                model.addAttribute("statusMessage", "Verified");
                model.addAttribute("message", "you have been verified, you can now order some services.");
                model.addAttribute("status", HttpStatus.OK);
            }
        } catch (Exception e) {
            System.out.println("error inside emailController: " + e.getMessage());
            model.addAttribute("statusMessage", "Something went wrong");
            model.addAttribute("message", "our server have encountered some problem :(, please try again later.");
            model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return "emailVerification";
    }

    @RequestMapping(value = "/see-all-services", method = RequestMethod.GET)
    public String seeAllServices() {
        return "adminService";
    }

    @RequestMapping(value = "/see-all-categories", method = RequestMethod.GET)
    public String seeAllCategories() {
        return "adminCategory";
    }

    @RequestMapping(value = "/admin-order-detail/{orderID}", method = RequestMethod.GET)
    public String seeDetailOrderAdmin(@PathVariable String orderID) {
        return "adminUpdateOrder";
    }
    @RequestMapping(value = "/see-all-cancel-request", method = RequestMethod.GET)
    public String seeCancelRequest() {
        return "adminCancelRequest";
    }
    @RequestMapping(value = "/dashboard")
    public String seeDashboard() {
        return "dashboard";
    }
}
