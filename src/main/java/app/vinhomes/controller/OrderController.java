package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Address;
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

    @GetMapping(path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Order> getAllOrder() {
//        Address address = Address.builder().buildingBlock("C4").buildingRoom("333").build();
//        Account account = Account.builder().accountName("Minh")
//                .password("12345")
//                .email("minh@gmail.com")
//                .accountName("minhtran")
//                .address(address)
//                .build();
//        accountRepository.save(account);
//        System.out.println("yes add a new account and building block success");
        return orderRepository.findAll();
    }

    @GetMapping(value = "/services", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Service> getAllServices() {

        return serviceRepository.findAll();
    }
    @PostMapping(value = "/createorder")
    public ResponseEntity<String> createOrder(@RequestBody JsonNode orderJSON, HttpServletRequest request) {
        LocalDate parsedDate = LocalDate.parse(orderJSON.get("day").asText());
        LocalTime startTime = timeSlotRepository.findById(orderJSON.get("timeId").asLong()).get().getStartTime();
        LocalDateTime orderedTime = parsedDate.atTime(startTime);
        //Date received is before now then "Date is in the past"
        if(parsedDate.isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Date is in the past");
        }

        if(orderedTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Passed this time");
        }

        Order order = orderService.officialCreateOrder(orderJSON, request);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("The timeslot is fully occupied");
        } else {
            return ResponseEntity.ok("Order created with Id " + order.getOrderId());
        }
    }

    
    //@PutMapping
    //@DeleteMapping

}
