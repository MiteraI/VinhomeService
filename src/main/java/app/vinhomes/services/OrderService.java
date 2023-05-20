package app.vinhomes.services;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.PaymentRepository;
import app.vinhomes.repository.ServiceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@org.springframework.stereotype.Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Order createOrder(JsonNode orderJson) {
        Long paymentId = orderJson.get("paymentId").asLong();
        Long serviceId = orderJson.get("serviceId").asLong();
        Long accountId = orderJson.get("accountId").asLong();

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Payment ID"));
        Service service = (Service) serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Service ID"));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Account ID"));

        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.of(2023,5,5,10,30))
                .endTime(LocalDateTime.of(2023,5,5,10,30))
                .build();

        Order order = new Order();
        order.setPayment(payment);
        order.setService(service);
        order.setAccount(account);
        order.setSchedule(schedule);
        order.setPrice(service.getPrice());
        schedule.setOrder(order);

        // Set other attributes of the order if needed

        return orderRepository.save(order);
    }
}
