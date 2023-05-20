package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.PaymentCategory;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.worker.WorkerStatus;
import jakarta.persistence.EntityNotFoundException;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private WorkerStatusRepository workerStatusRepository;
    @Test
    public void addOrder() {
        Account account = accountRepository.findById(1L).get();
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.of(2023,5,17,10,30))
                .endTime(LocalDateTime.of(2023,5,18,0,0))
                .build();
        Order order = Order.builder()
                .createTime(LocalDateTime.now().withNano(0))
                .price(600000)
                .account(account)
                .service(serviceRepository.findById(1L).get())
                .payment(paymentRepository.findById(1L).get())
                .schedule(schedule)
                .build();
        schedule.setOrder(order);
        orderRepository.save(order);
    }
    @Test
    public void addOrderThatAssignWorker() {
        Account account = accountRepository.findById(1L).get();
        Service service = serviceRepository.findById(1L).get();
        Schedule schedule = Schedule.builder()
                .startTime(LocalDateTime.of(2023,5,17,10,30))
                .endTime(LocalDateTime.of(2023,5,18,0,0))
                .build();
        Order order = Order.builder()
                .createTime(LocalDateTime.now().withNano(0))
                .price(600000)
                .account(account)
                .service(service)
                .payment(paymentRepository.findById(1L).get())
                .schedule(schedule)
                .build();
        schedule.setOrder(order);
        if (service.getNumOfPeople() == 2) {
            List<WorkerStatus> workerStatuses = workerStatusRepository
                    .findTop2ByServiceCategoryOrderByWorkCountAsc(
                            service.getServiceCategory());
            for (WorkerStatus workerStatus : workerStatuses) {
                schedule.addWorker(workerStatus.getAccount());
            }
        }
        orderRepository.save(order);
    }
    @Test
    public void printAllOrder() {
        System.out.println("Orders info = " + orderRepository.findAll());
    }

    @Test
    public void selectAllOrder() {
        System.out.println("Orders info = " + orderRepository.findAll());
    }
}