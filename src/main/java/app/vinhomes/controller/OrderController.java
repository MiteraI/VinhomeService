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
}



