package app.vinhomes.service;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.worker.Leave;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.*;
import app.vinhomes.repository.order.*;
import app.vinhomes.repository.worker.LeaveRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
    @Autowired
    private LeaveRepository leaveRepository;
    @Autowired
    private PaymentCategoryRepository paymentCategoryRepository;
    private Order officialCreateOrder( HttpServletRequest request) {//JsonNode orderJson,
        System.out.println("inside OfficialCreateOrder");
        HttpSession session = request.getSession();
        Account sessionAccount = (Account) session.getAttribute("loginedUser");
        if (sessionAccount == null) {
            return new Order();
        }
        //Get account from session
        Account account = accountRepository.findById(sessionAccount.getAccountId()).get();

        //Assigning service
        Long serviceId = Long.parseLong(request.getParameter("serviceId") ); //orderJson.get("serviceId").asLong();

        Service service = serviceRepository.findById(serviceId).get();

        //Assigning timeslot
        Long timeId = Long.valueOf(request.getParameter("optionTime")); //orderJson.get("timeId").asLong();
        TimeSlot timeSlot = timeSlotRepository.findById(timeId).get();

        //Assigning Payment
        Long paymentId = Long.valueOf(request.getParameter("transactionMethod"));//orderJson.get("paymentId").asLong();
        Payment payment = paymentRepository.findById(paymentId).get();

        //Assigning schedule
        String day = request.getParameter("day"); //orderJson.get("day").asText();
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
        //Find the workers that is in the allowed day off
        System.out.println(schedule.getWorkDay());
        List<Leave> leaveList = leaveRepository.findByLeaveDay(schedule.getWorkDay());
        List<Account> offWorkerAccounts = new ArrayList<>();
        for (Leave leave : leaveList) {
            offWorkerAccounts.add(leave.getAccount());
        }

        List<Account> readyWorkerAccounts = new ArrayList<>();
        if (!offWorkerAccounts.isEmpty()) {
            for (Account availableWorker : workerAccounts) {
                boolean isOff = false;
                for (Account offWorker : offWorkerAccounts) {
                    if (offWorker.getAccountId() == availableWorker.getAccountId()) {
                        isOff = true;
                        break;
                    }
                }
                if (!isOff) {
                    readyWorkerAccounts.add(availableWorker);
                }
            }
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
        if (busyWorkerAccounts.isEmpty() && offWorkerAccounts.isEmpty()) {
            freeWorkerAccounts = workerAccounts;
        } else {
            for (Account worker : readyWorkerAccounts) {
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
                .payment(payment)
                .schedule(schedule)
                .status(OrderStatus.PENDING)
                .build();
        schedule.setOrder(order);
        return orderRepository.save(order);
    }
    public ResponseEntity<String> createOrder(  HttpServletRequest request) {//JsonNode orderJSON,
        System.out.println("inside createOrder");
        LocalDate parsedDate = LocalDate.parse(request.getParameter("day"));//orderJSON.get("day").asText()
        LocalTime startTime = timeSlotRepository.findById(Long.valueOf(request.getParameter("optionTime"))).get().getStartTime();//orderJSON.get("timeId").asLong()
        LocalDateTime orderedTime = parsedDate.atTime(startTime);
        if (parsedDate.isBefore(LocalDate.now())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Date is in the past");
        }

        if (orderedTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Passed this time");
        }

        Order order = this.officialCreateOrder( request);//orderJSON,
        System.out.println("pass official create order");
        if (order == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("The timeslot is fully occupied");
        } else {
            if (order.getAccount() == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Have not logged in");
            }
            return ResponseEntity.ok(order.getOrderId().toString());
        }
    }
    public Order getOrderById(String order_id){
        try{
            long parsedOrderId = Long.parseLong(order_id);
            Order getOrder = orderRepository.findById(parsedOrderId).get();
            return getOrder;
        }catch (NoSuchElementException e){
            System.out.println("erroer in OrderService: "+ e.getMessage());
            return null;
        }catch (NumberFormatException e){
            System.out.println("erroer in OrderService: "+ e.getMessage());
            return null;
        }
    }
}
