package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.repository.*;
import app.vinhomes.repository.order.PaymentRepository;
import app.vinhomes.repository.order.TimeSlotRepository;
import app.vinhomes.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

//@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("RC_REF_COMPARISON")
@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getAllOrders () {
        List<Order> allOrders =  orderRepository.findAll();
        List<Map<String, Object>> getAllOrders = new ArrayList<>();
        for (Order o : allOrders) {
            Account account = o.getAccount();
            Service service = o.getService();
            Payment payment = o.getPayment();
            Transaction transaction = transactionRepository.findById(o.getOrderId()).get();
            Map<String, Object> detailOrder = new HashMap<>();
            detailOrder.put("order", o);
            detailOrder.put("account", account);
            detailOrder.put("service", service);
            detailOrder.put("payment", payment);
            detailOrder.put("transaction", transaction);
            getAllOrders.add(detailOrder);
        }
        return ResponseEntity.ok(getAllOrders);
    }

    @PostMapping(value = "/ratecomment")
    public ResponseEntity<String> rate(@RequestBody JsonNode rateJson) {

        return ResponseEntity.ok().body("Reviewed");
    }


    @GetMapping(value = "/yourOrders", produces = MediaType.APPLICATION_JSON_VALUE)
    //get all orders of that logged in account//
    public ResponseEntity<List<Order>> getYourOrders(HttpSession session) {
        Account account = (Account) session.getAttribute("loginedUser");
        if (account == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else {
            return ResponseEntity.ok(orderRepository.findAllByAccount(account));
        }
    }

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping(value = "/payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Payment> getAllPaymentMethod() {
        return paymentRepository.findAll();
    }

    @GetMapping(value = "/timeSlot", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TimeSlot> getAllTimeSlot() {
        return timeSlotRepository.findAll();
    }

    @GetMapping(value = "/services/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Order> getOrderbyServiceId(@PathVariable("id") Long serviceId) {
        return orderRepository.findAllByService_ServiceId(serviceId);
    }

    @GetMapping(value = "/getSession", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> getUsername(HttpSession session) {
        //System.out.println("4");
        System.out.println("get session");
        Account getAccount = (Account) session.getAttribute("loginedUser");
        getAccount = accountRepository.findById(getAccount.getAccountId()).get();
        session.setAttribute("loginedUser",getAccount);
        return ResponseEntity.status(HttpStatus.OK).body(getAccount);
    }

    @GetMapping(value = "/getSession/{option}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPhone(@PathVariable("option") String option, HttpSession session){
        switch (option.toLowerCase()){
            case "f":
                return  ResponseEntity.status(HttpStatus.OK).body((ArrayList<Phone>) session.getAttribute("phone"));
            default:
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("this option not exist");
        }

    }

    @PostMapping(value = "/review", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postReview(@RequestBody JsonNode reviewJSON, HttpSession session) {
        Account account = (Account) session.getAttribute("loginedUser");
        Long orderId = reviewJSON.get("orderId").asLong();
        String comment = reviewJSON.get("comment").asText();
        int rating = reviewJSON.get("rating").asInt();
        if (account == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else {
            //update comment and rating of the order//
            Order order = orderRepository.findById(orderId).get();
            if(order.getStatus() != OrderStatus.SUCCESS){
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Must complete order to comment");
            }
            order.setComment(comment);
            order.setRating(rating);
            orderRepository.save(order);
            return ResponseEntity.ok("Review posted");
        }
    }

    @GetMapping(value = "/yourOrders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getYourOrder(@PathVariable("orderId") Long orderId, HttpSession session) {
        Account account = (Account) session.getAttribute("loginedUser");
        System.out.println("getYourOrder: "+ account);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else {
            Order order = orderRepository.findById(orderId).get();
            if (order.getAccount().getAccountId() == account.getAccountId()) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        }
    }

    @GetMapping(value = "/get-orders-to-confirm", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getAllOrdersofOneWorkerToConfirm (HttpServletRequest request) {
        List<Map<String, Object>> listOrdersDetail = new ArrayList<>();
        HttpSession session = request.getSession();
        Account worker = (Account) session.getAttribute("loginedUser");
        if (worker != null) {
            List<Order> listOrders = orderService.getOrdersOfOneWorkerForConfirmation(worker);
            for (Order o : listOrders) {
                Account account = o.getAccount();
                Service service = o.getService();
                Schedule schedule = o.getSchedule();
                Map<String, Object> orderDetail = new HashMap<>();
                if (account != null) {
                    orderDetail.put("account", account);
                    orderDetail.put("order", o);
                    orderDetail.put("service", service);
                    orderDetail.put("schedule", schedule);
                    listOrdersDetail.add(orderDetail);
                }
            }
        }
        return ResponseEntity.ok(listOrdersDetail);
    }
    @GetMapping(value = "/getOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getOrder(@PathVariable("orderId") Long orderId, HttpSession session) {
        Account account = (Account) session.getAttribute("loginedUser");
        System.out.println("getYourOrder: "+ account);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else {
            Order getOrder= orderRepository.findById(orderId).get();
            return ResponseEntity.status(HttpStatus.OK).body(getOrder);
        }
    }
}



