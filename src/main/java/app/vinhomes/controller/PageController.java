package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.entity.order.Service;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.repository.order.ServiceCategoryRepository;

import app.vinhomes.repository.order.ServiceRepository;
import app.vinhomes.service.ServiceTypeService;
import jakarta.persistence.criteria.CriteriaBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.*;
import java.util.stream.Collectors;



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
    public String getAllServiceOfCategory(@PathVariable("id") Long categoryId, Model model){
        model.addAttribute("services", serviceCategoryRepository.findById(categoryId).get());
        model.addAttribute("ratingArr", new int[]{5, 4, 3, 2, 1});
        return "categoryservices";
    }

    @RequestMapping(value = "/service/{serviceId}")
    public String prepareOrder (@PathVariable("serviceId") Long serviceId, Model model, HttpServletRequest request) {
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

    public Map<Integer, Map<Integer, Integer>> ratingMap(List<Integer> serviceId) {
        Map<Integer, Map<Integer, Integer>> serviceRatingMap = new HashMap<Integer, Map<Integer, Integer>>();
        int maxRating = 5, sameRating = 0;
        for (int i = 0; i < serviceId.size(); i++) {
            Map<Integer, Integer> ratingMap = new HashMap<Integer, Integer>();
            for (int j = 1; j <= maxRating; j++) {
                sameRating = orderRepository.COUNT_RATING_FOR_SERVICE(serviceId.get(i), j);
                ratingMap.put(j, sameRating);
            }
            serviceRatingMap.put(serviceId.get(i), ratingMap);
        }
        for (Map.Entry<Integer, Map<Integer, Integer>> serviceRating : serviceRatingMap.entrySet()) {
            System.out.println("key: " + serviceRating.getKey() + " value: " + serviceRating.getValue());
        }
        return serviceRatingMap;
    }

    public Map<Integer, Float> avgRatingForEachService(List<Integer> serviceId) {
        Map<Integer, Float> avgRatingEachService = new HashMap<>();
        Map<Integer, Map<Integer, Integer>> allRatingForEachService = new HashMap<>();
        allRatingForEachService = ratingMap(serviceId);
        int maxRating = 5, totalRating = 0, sumOfAllRating = 0;
        float avgRatingService = 0;
        for (Map.Entry<Integer, Map<Integer, Integer>> rating : allRatingForEachService.entrySet()) {
            totalRating = 0;
            sumOfAllRating = 0;
            for (int i = 1; i <= maxRating; i++) {
                totalRating += rating.getValue().get(i);
                sumOfAllRating += rating.getValue().get(i) * i;
            }
            avgRatingService = (float) sumOfAllRating / totalRating > 0 ? Math.round((float) sumOfAllRating / totalRating) : 0;
            avgRatingEachService.put(rating.getKey(), avgRatingService);
        }
        for (Map.Entry<Integer, Float> avgRating : avgRatingEachService.entrySet()) {
            System.out.println("key: " + avgRating.getKey() + " values: " + avgRating.getValue());
        }
        return avgRatingEachService;
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

    @RequestMapping(value = "/verification",method = RequestMethod.GET)
    public String verification(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session == null){
            return "login";
        }
        return "verification";
    }

    @RequestMapping(value = "/otp-verification",method = RequestMethod.GET)
    public String otpVerification(HttpServletRequest request, Model model) {
        Account acc = getSessionAccount(request);
        if (acc == null) {
            return "redirect:/login";
        }
        model.addAttribute("acc", acc.getAccountName());
        return "otpverification";
    }
    @RequestMapping(value = "/forget_Account",method = RequestMethod.GET)
    public String forgetAccount(){
        return "forgetAccount";
    }

    @RequestMapping(value = "/TESTBED", method = RequestMethod.GET)
    public String TESTBED() {
        return "TESTBED";
    }

}
