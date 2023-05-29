package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.*;
import app.vinhomes.services.OrderService;
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
import java.util.List;

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



    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }

    @PostMapping(value = "/createorder")
    public ResponseEntity<String> createOrder(@RequestBody JsonNode orderJSON, HttpServletRequest request) {
        LocalDate parsedDate = LocalDate.parse(orderJSON.get("day").asText());
        LocalTime startTime = timeSlotRepository.findById(orderJSON.get("timeId").asLong()).get().getStartTime();
        LocalDateTime orderedTime = parsedDate.atTime(startTime);
        //Date received is before now then "Date is in the past"
        if (parsedDate.isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Date is in the past");
        }

        if (orderedTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Passed this time");
        }

        Order order = orderService.officialCreateOrder(orderJSON, request);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("The timeslot is fully occupied");
        } else {
            if (order.getAccount() == null) return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Have not logged in");
            return ResponseEntity.ok("Order created with Id " + order.getOrderId());
        }
    }

    @PostMapping(value = "/ratecomment")
    public ResponseEntity<String> rate(@RequestBody JsonNode rateJson) {

        return ResponseEntity.ok().body("Reviewed");
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@RequestBody JsonNode loginJson, HttpSession session) {
        Account account = accountRepository.findByAccountNameAndPassword(
                loginJson.get("accountname").asText(),
                loginJson.get("pwd").asText()
        );
        if (account != null) {
            session.setAttribute("loginedUser", account);
            return ResponseEntity.ok().body("Success");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No account found");
        }
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
    public List<Order> getOrderbyServiceId (@PathVariable("id") Long serviceId) {
        return orderRepository.findAllByService_ServiceId(serviceId);
    }

    @GetMapping("/getSession")
    public Account getUsername(HttpSession session) {
        return (Account) session.getAttribute("loginedUser");
    }

    @PostMapping(value = "/review/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postReview(@PathVariable ("orderId") Long orderId, @RequestBody JsonNode reviewJSON, HttpSession session) {
        Account account = (Account) session.getAttribute("loginedUser");
        String comment = reviewJSON.get("comment").asText();
        int rating = reviewJSON.get("rating").asInt();
        if (account == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } else {
            //update comment and rating of the order//
            Order order = orderRepository.findById(orderId).get();
            order.setComment(comment);
            order.setRating(rating);
            orderRepository.save(order);
            return ResponseEntity.ok("Review posted");
        }
    }

    @GetMapping(value = "/yourOrders/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getYourOrder(@PathVariable ("orderId") Long orderId, HttpSession session) {
        Account account = (Account) session.getAttribute("loginedUser");
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
}




