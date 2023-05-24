package app.vinhomes.services;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Review;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private WorkerStatusRepository workerStatusRepository;
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    public Order officialCreateOrder(JsonNode orderJson, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Account sessionAccount = (Account) session.getAttribute("loginedUser");
        if (sessionAccount == null) {
            return null;
        }
        //Get account from session
        Account account = accountRepository.findById(sessionAccount.getAccountId()).get();

        //Assigning service
        Long serviceId = orderJson.get("serviceId").asLong();
        Service service = serviceRepository.findById(serviceId).get();

        //Assigning timeslot
        Long timeId = orderJson.get("timeId").asLong();
        TimeSlot timeSlot = timeSlotRepository.findById(timeId).get();

        //Assigning schedule
        String day = orderJson.get("day").asText();
        Schedule schedule = Schedule.builder()
                .workDay(LocalDate.parse(day))
                .timeSlot(timeSlot)
                .build();

        //Initialize list of appropriate workers statuses for find worker account
        List<WorkerStatus> workerStatuses = workerStatusRepository.findByServiceCategoryAndStatusOrderByWorkCountAsc(
                serviceCategoryRepository.findById(service.getServiceCategory().getServiceCategoryId()).get()
                , 0
        );

        //Transfer to worker account to list
        List<Account> workerAccounts = new ArrayList<>();
        for (WorkerStatus workerStatus : workerStatuses) {
            workerAccounts.add(workerStatus.getAccount());
        }

        //Get order that is in the work day and timeslot the user chose with the job cate to find busy worker
        List<Order> orders = orderRepository.
                findAllBySchedule_WorkDayAndSchedule_TimeSlotAndService_ServiceCategory(
                        schedule.getWorkDay()
                        , timeSlot
                        //Find serviceCate / job to know the workers
                        , serviceCategoryRepository.findById(service.getServiceCategory().getServiceCategoryId()).get()
                );

        //Get busy worker list to exclude from the worker account list -> free workers ready to be assigned
        List<Account> busyWorkerAccounts = new ArrayList<>();
        for (Order order : orders) {
            busyWorkerAccounts.addAll(order.getSchedule().getWorkers());
        }

        //Get free worker list from the 2 other list
        List<Account> freeWorkerAccounts = new ArrayList<>();
        if (busyWorkerAccounts.size() == 0) {
            freeWorkerAccounts = workerAccounts;
        } else {
            for (Account worker : workerAccounts) {
                boolean isBusy = false;
                for (Account busyWorker : busyWorkerAccounts) {
                    if (busyWorker.getAccountId() == worker.getAccountId()) {
                        isBusy = true;
                        break;
                    }
                }
                if (!isBusy) {
                    freeWorkerAccounts.add(worker);
                }
            }
        }
        try {
            switch (service.getNumOfPeople()) {
                case 1 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 1));
                case 2 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 2));
                case 3 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 3));
                case 4 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 4));
            }
        } catch (Exception ex) {
            System.out.println("Not enough worker");
            return null;
        }
        Order order = Order.builder()
                .createTime(LocalDateTime.now().withNano(0))
                .price(service.getPrice())
                .account(account)
                .service(service)
                .payment(paymentRepository.findById(1L).get())
                .schedule(schedule)
                .build();
        schedule.setOrder(order);
        Review review = Review.builder()
                .account(account)
                .service(service)
                .build();
        order.setReview(review);
        return orderRepository.save(order);
    }
}
