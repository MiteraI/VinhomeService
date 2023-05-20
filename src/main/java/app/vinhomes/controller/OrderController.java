package app.vinhomes.controller;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Address;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
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

    @GetMapping(value = "/accounts" , produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping(value = "/schedules" , produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @PostMapping(path = "/addAccount", produces = MediaType.APPLICATION_JSON_VALUE)
    public  Account insertAccount(@RequestBody Account account){
        return null;

    }
    //@PutMapping
    //@DeleteMapping

}
