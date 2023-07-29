package app.vinhomes.service;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.order.Payment;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.worker.Leave;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.*;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.repository.order.*;
import app.vinhomes.repository.worker.LeaveRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.time.format.DateTimeParseException;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import static app.vinhomes.common.SessionUserCaller.getSessionUser;

@org.springframework.stereotype.Service
public class OrderService {
    private List<Order> InvalidCancelOrder = new LinkedList<>();
    @Value("${time.hourpolicy}")
    private int HourPolicy;
    @Value("${order.policy.day_before_service}")
    private String DAY_POLICY_ORDER;
    @Value("${order.policy.hour_before_service}")
    private String HOUR_POLICY_ORDER;
    @Value("${order.policy.max_day_prior_to_service}")
    private String DAY_PRIOR_ORDER;
    @Value(value = "${order.policy.max_order_a_day}")
    private int ORDER_MAX;
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
    private PhoneRepository phoneRepository;
    @Autowired
    private PaymentCategoryRepository paymentCategoryRepository;
    @Autowired
    private WorkerService workerService;
    @Autowired
    private TransactionRepository transactionRepository;
    //Do this because can't access transaction from order
    public List<Transaction> getCustomerTransactions(HttpServletRequest request) {
        Account loginedUser = getSessionUser(request);
        List<Transaction> transactionList = transactionRepository.findAllByOrder_Account_AccountId(loginedUser.getAccountId());
        List<Transaction> reversedTransactionList = new ArrayList<Transaction>();
        for (int i = transactionList.size() - 1; i >= 0; i--) {
            reversedTransactionList.add(transactionList.get(i));
        }
        return reversedTransactionList;
    }

    public Order getCustomerOrderDetails(HttpServletRequest request, Long orderId) {
        Account loginedUser = getSessionUser(request);
        return orderRepository.findByAccount_AccountIdAndOrderId(loginedUser.getAccountId(), orderId);
    }
    
    public Order officialCreateOrder(JsonNode orderJson, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Account sessionAccount = (Account) session.getAttribute("loginedUser");
        if (sessionAccount == null) {
            return new Order();
        }
        //Get account from session
        Account account = accountRepository.findById(sessionAccount.getAccountId()).get();

        //Assigning service
        Long serviceId =orderJson.get("serviceId").asLong(); //Long.parseLong(request.getParameter("serviceId") ); //

        Service service = serviceRepository.findById(serviceId).get();

        //Assigning timeslot
        Long timeId =orderJson.get("timeId").asLong();// Long.valueOf(request.getParameter("optionTime")); //
        TimeSlot timeSlot = timeSlotRepository.findById(timeId).get();

        //Assigning Payment
        Long paymentId = orderJson.get("paymentId").asLong();//Long.valueOf(request.getParameter("transactionMethod"));//
        Payment payment = paymentRepository.findById(paymentId).get();

        //Assigning schedule
        String day =orderJson.get("day").asText();// request.getParameter("day"); //
        Schedule schedule = Schedule.builder()
                .workDay(LocalDate.parse(day))
                .timeSlot(timeSlot)
                .build();
        Long phoneId = orderJson.get("phonenumberId").asLong();
        String phonenumber = phoneRepository.findById(phoneId).get().getNumber();
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
                    //long offId = offWorker.getAccountId();
                    //long avaId = availableWorker.getAccountId();
                    if (offWorker.getAccountId() == availableWorker.getAccountId() ) {//offId == avaId
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
                findAllBySchedule_WorkDayAndSchedule_TimeSlotAndService_ServiceCategoryAndStatus(
                        schedule.getWorkDay()
                        , timeSlot
                        //Find serviceCate / job to know the workers
                        , serviceCategoryRepository.findById(service.getServiceCategory().getServiceCategoryId()).get()
                        , OrderStatus.PENDING
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
                case 5 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 5));
                case 6 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 6));
                case 7 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 7));
                case 8 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 8));
                case 9 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 9));
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
                .phoneNumber(phonenumber)
                .build();
        schedule.setOrder(order);
        return orderRepository.save(order);
    }

    public ResponseEntity<String> createOrder(JsonNode orderJSON, HttpServletRequest request) {//JsonNode orderJSON,
        try {
            System.out.println("inside createOrder");
            LocalDate parsedDate = LocalDate.parse(orderJSON.get("day").asText());//request.getParameter("day")
            LocalTime startTime = timeSlotRepository.findById(Long.valueOf(orderJSON.get("timeId").asLong())).get().getStartTime();////request.getParameter("optionTime"))
            LocalDateTime orderedTime = parsedDate.atTime(startTime);
            if (parsedDate.isBefore(LocalDate.now())) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Date is in the past");
            }
            if (orderedTime.isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Passed this time");
            }
            Order order = this.officialCreateOrder(orderJSON, request);//
            System.out.println("pass official create order");
            if (order == null) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("the timeslot is fully occupied (not enough workers)");
            } else {
                if (order.getAccount() == null) {
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Have not logged in");
                }
                return ResponseEntity.ok(order.getOrderId().toString());
            }
        }catch (NoSuchElementException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }catch (DateTimeParseException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        }

    }
    public Order getOrderById(Long order_id){
        try{
            Order getOrder = orderRepository.findById(order_id).get();
            return getOrder;
        }catch (NoSuchElementException e){
            System.out.println("erroer in OrderService: "+ e.getMessage());
            return null;
        }catch (NumberFormatException e){
            System.out.println("erroer in OrderService: "+ e.getMessage());
            return null;
        }
    }
    public boolean updateOrderByObject(Order order){
        try{
            orderRepository.save(order);
            return true;
        }catch (Exception e){
            System.out.println("update error: "+ e.getMessage());
            return false;
        }
    }
    public boolean checkIfOrderIsPending_IsExist(Long orderId){
        try{
            Order getOrder = orderRepository.findById(orderId).get();
            if(getOrder.getStatus().equals(OrderStatus.PENDING)){
                return true;
            }
            return false;
        }catch (NullPointerException e){
            return false;
        }
        catch (Exception e){
            return false;
        }
    }
    public boolean checkIfOver_2_hourPolicy(String orderId){
        try{
            long parsedOrderId = Long.parseLong(orderId);
            Order getOrder = orderRepository.findById(parsedOrderId).get();
            // neu + 2 tieng ma van before, tuc la da qua 2 tieng => no refund (boolean = true)
            // else cho refund   (boolean = false)
            boolean isCreateTimePassLimit =  getOrder.getCreateTime().plusHours(HourPolicy).isBefore(LocalDateTime.now());
            if(isCreateTimePassLimit == false){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public boolean checkIfOrderLegitForRefund(String orderId){
        try{
            long parsedOrderId = Long.parseLong(orderId);
            Order getOrder = orderRepository.findById(parsedOrderId).get();
            if(getOrder.getStatus().equals(OrderStatus.CANCEL)|| getOrder.getStatus().equals(OrderStatus.SUCCESS)){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    //Ham nay de lay ra cac order can confirm cua 1 account worker
    public List<Order> getOrdersOfOneWorkerForConfirmation (Account account) {
        Account checkAccount = null;
        List<Order> listOrders = new ArrayList<>();
        List<Order> listAllOrders = orderRepository.findAll();
        for (Order o : listAllOrders) {
            if (o.getStatus().equals(OrderStatus.PENDING)) {
                checkAccount = workerService.getWorkerOfOneOrderForConfirmation(o.getOrderId());
                if (checkAccount.getAccountId() == account.getAccountId()) {
                    listOrders.add(o);
                }
            }
        }
        return listOrders;
    }

    public boolean checkDayValidForOrder(String orderDate, LocalTime orderTimeSlot_StartTime){
        LocalDate parseDate = LocalDate.parse(orderDate);
        LocalDate dateNow = LocalDate.now();
        long getDayDiff = ChronoUnit.DAYS.between(dateNow,parseDate);
        long getHourDiff = ChronoUnit.HOURS.between(LocalTime.now(),orderTimeSlot_StartTime);
        if(getDayDiff < Integer.parseInt(DAY_POLICY_ORDER.trim())){
            System.out.println("date is off policy " +getDayDiff);
            if(getHourDiff <= Integer.parseInt(HOUR_POLICY_ORDER.trim())){
                System.out.println("hour is also off policy " +getHourDiff);
                return false;
            }else{
                System.out.println("hour is in policy, success "+ getHourDiff);
                return true;
            }
        }else{
            if(getDayDiff > Integer.parseInt(DAY_PRIOR_ORDER.trim())){
                System.out.println("date violate max prior day order");
                return false;
            }
            System.out.println("date is in policy, success");
            return true;
        }
    }
    public boolean checkDayValidForOrder(String orderDay, String timeSlotId){
        try{
            Long parsedTimeSlotId = Long.parseLong(timeSlotId);
            TimeSlot getTimeSlot = timeSlotRepository.findById(parsedTimeSlotId).get();
            LocalTime getStartTime = getTimeSlot.getStartTime();
            return this.checkDayValidForOrder(orderDay,getStartTime);
        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
            return false;
        }
    }

        public void setOrderStatus (Order order, OrderStatus status){
        order.setStatus(status);
        orderRepository.save(order);
    }
    public List<Order> getInvalidCancelOrder() {
        return this.InvalidCancelOrder;
    }
    public void addInvalidCancelOrder(Order order){
        List<Order> getList = getInvalidCancelOrder();
        for(Order item : getList){
            if(item.getOrderId().equals(order.getOrderId())){
                System.out.println("this order is already in the List");
                return;
            }
        }
        getList.add(order);
    }
    public boolean checkIfReachMaxOrder(Long userId){
        LocalDate localDate = LocalDate.now();
        LocalDateTime getStart = localDate.atStartOfDay();
        LocalDateTime getEnd = localDate.plusDays(1).atStartOfDay().minusSeconds(1);
        //System.out.println(getStart + "    "+ getEnd);
        List<Order> getAllOrder = orderRepository.findAllByCreateTimeBetweenAndAccount_AccountId(getStart,getEnd,userId);
        System.out.println(getAllOrder.size());
        int size = getAllOrder.size();
        if(size >= ORDER_MAX){
            return true;
        }else{
            return false;
        }
    }
}
