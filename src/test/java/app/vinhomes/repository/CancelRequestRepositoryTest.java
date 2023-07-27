package app.vinhomes.repository;


import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.type_enum.CancelRequestStatus;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.worker.CancelRequest;
import app.vinhomes.repository.worker.CancelRequestRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CancelRequestRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CancelRequestRepository cancelRequestRepository;
    @Test
    @Disabled
    void testCancelRequest(){
        LocalDateTime timeNow = LocalDateTime.now();
        long orderId = 37;
        long workerId = 2;
        app.vinhomes.entity.Order getOrder = orderRepository.findByOrderId(orderId);
        Account getWorker = accountRepository.findByAccountId(workerId);
        CancelRequest create = CancelRequest.builder()
                .fileURL("adsfadsds")
                .reason("asdfasdfds")
                .timeCancel(timeNow)
                .worker(getWorker)
                .order(getOrder)
                .status(CancelRequestStatus.PENDING)
                .build();
        cancelRequestRepository.save(create);
    }
    @Test
    void getCancelRequest(){
        CancelRequest getRequest = cancelRequestRepository.findById(1l).get();
        System.out.println(getRequest.toString());
        app.vinhomes.entity.Order getOrder = getRequest.getOrder();
        Account getWorker = getRequest.getWorker();
        System.out.println(getOrder);
        System.out.println(getWorker);

    }
    @Test
    void checkForCancel(){
        CancelRequest getRequest = cancelRequestRepository.findById(1l).get();
        Order getOrder = getRequest.getOrder();
        System.out.println(getRequest.getStatus());
        if(getRequest.getStatus() == CancelRequestStatus.PENDING){
            OrderStatus getStatus = getOrder.getStatus();
            if(getStatus == OrderStatus.PENDING){
                System.out.println("yes inside dealing with logic");
                getRequest.setStatus(CancelRequestStatus.ACCEPT);
                cancelRequestRepository.save(getRequest);
            }
            System.out.println("Order is not pending");
            getRequest.setStatus(CancelRequestStatus.REJECT);
            cancelRequestRepository.save(getRequest);
            return;
        }
        //cancelRequestRepository.save(getRequest);
        System.out.println("request is not pending");
    }
}