package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Address;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.*;
import app.vinhomes.services.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private WorkerStatusRepository workerStatusRepository;

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

    @GetMapping(value = "/payments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping(value = "/schedules", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @GetMapping(value = "/workerstatuses")
    public List<WorkerStatus> getAllStatuses() { return workerStatusRepository.findAll(); }

    @GetMapping("/getSession")
    public Account getUsername(HttpSession session) {
        return (Account) session.getAttribute("loginedUser");
    }

    @PostMapping(value = "/addorder")
    public ResponseEntity<String> createOrder(@RequestBody JsonNode orderJSON) {
        Order order = orderService.createOrder(orderJSON);
        return ResponseEntity.ok("Order created with Id " + order.getOrderId());
    }

    
    //@PutMapping
    //@DeleteMapping

}
