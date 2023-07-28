package app.vinhomes.unittest;

import app.vinhomes.controller.WorkerApi;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.type_enum.CancelRequestStatus;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.worker.CancelRequest;
import app.vinhomes.entity.worker.LeaveReport;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.repository.order.ScheduleRepository;
import app.vinhomes.repository.order.TimeSlotRepository;
import app.vinhomes.repository.worker.CancelRequestRepository;
import app.vinhomes.repository.worker.LeaveReportRepository;
import app.vinhomes.security.email.email_service.EmailService;
import app.vinhomes.security.esms.otp_service.ESMSservice;
import app.vinhomes.service.OrderService;
import app.vinhomes.service.ScheduleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@SpringBootTest
//@DataJpaTest
//@ExtendWith(MockitoExtension.class)// this is to replace the mock config, not sure
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class test2 {
    @Value("${order.policy.day_before_service}")
    private String DAY_POLICY_ORDER;
    @Value("${order.policy.hour_before_service}")
    private String HOUR_POLICY_ORDER;
    @Value("${order.policy.max_day_prior_to_service}")
    private String DAY_PRIOR_ORDER;
    private final String accessTokenSpeedSMS = "ef_4sDm1PIiI6GSAF_Lx3iXbTV593Zts";
    @Autowired
    private TimeSlotRepository timeSlotRepository;
    @Autowired
    private WorkerApi workerApi;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private EmailService emailService;
    @Test
    void testGetPolicyOrder(){
        Assertions.assertEquals("1",DAY_POLICY_ORDER);
        Assertions.assertEquals("8",HOUR_POLICY_ORDER);
    }
    @Test
    void testCheckDate(){
        String getDate = "2023-08-01";
        LocalDate parseDate = LocalDate.parse(getDate);
        LocalDate dateNow = LocalDate.now();
        System.out.println();

        TimeSlot getTimeSlot1= timeSlotRepository.findById(3l).get();
        TimeSlot getTimeSlot2= timeSlotRepository.findById(5l).get();
        System.out.println(parseDate);
        System.out.println(LocalDate.now());
        System.out.println(getTimeSlot1.getStartTime());
        System.out.println(getTimeSlot2.getStartTime());
        System.out.println(LocalTime.now());
        boolean getResult1 = checkDayValidForOrder("2023-08-01",getTimeSlot1.getStartTime());
        Assertions.assertEquals(false,getResult1);
        boolean getResult2 = checkDayValidForOrder("2023-07-15",getTimeSlot1.getStartTime());
        Assertions.assertEquals(true,getResult2);
        boolean getResult3 = checkDayValidForOrder("2023-07-03",getTimeSlot2.getStartTime());
        Assertions.assertEquals(false,getResult3);
        boolean getResult4 = checkDayValidForOrder("2023-07-03",getTimeSlot1.getStartTime());
        Assertions.assertEquals(true,getResult4);
        System.out.println(getResult4);

    }
    //String for orderdate because we get it from jsonNode as text
    private boolean checkDayValidForOrder(String orderDate, LocalTime orderTimeSlot_StartTime){
        TimeSlot getFirstTimeSlot = timeSlotRepository.findById(1l).get();
        LocalDate parseDate = LocalDate.parse(orderDate);
        LocalDate dateNow = LocalDate.now();
        long getDayDiff = ChronoUnit.DAYS.between(dateNow,parseDate);
        long getHourDiff = ChronoUnit.HOURS.between(LocalTime.now(),orderTimeSlot_StartTime);//LocalTime.now()
        if(getDayDiff <= Integer.parseInt(DAY_POLICY_ORDER.trim())){
            System.out.println("date is off policy " +getDayDiff);
            if(getHourDiff < Integer.parseInt(HOUR_POLICY_ORDER.trim())){
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

    @Test
    void testESMS() throws IOException {
        ESMSservice service = new ESMSservice();
        service.sendGetJSON("0847942496","test ting ");
    }
    @Test
    void testGetFeeWorker(){
        List<Account> getFreeWorker = workerApi.getFreeWorkerAtTimeSLot(88l);
    }
    @Test
    void test(){
        LocalDate get = LocalDate.parse("2023-07-11");
        List<Schedule> getSche = scheduleRepository.findAllByWorkDay(get);
        getSche.forEach(item ->{
            System.out.println(item);
        });
    }
    @Test
    void test2(){
        LocalDate get = LocalDate.parse("2023-07-11");
        Set<Account> getAcc = scheduleService.getWorkersAccountMatchWorkday_TimeSlot(get,6l);
        getAcc.forEach(item ->{
            System.out.println(item);
        });
    }
    @Autowired
    private CancelRequestRepository cancelRequestRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Test
    @Disabled
    void testCancelRequest(){
        LocalDateTime timeNow = LocalDateTime.now();
        long orderId = 96;
        long workerId = 2;
        Order getOrder = orderRepository.findByOrderId(orderId);
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
        Order getOrder = getRequest.getOrder();
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
    @Test
    void requestForCancel(){
        long orderId = 1l;
        long workerId = 2l ;
        String reason = "adsfsadf";
        String urlImage = "asdfsadf";
        Order getOrder = orderRepository.findByOrderId(orderId);
        Account getWorker =accountRepository.findByAccountId(workerId);
        if(getOrder == null || getWorker == null){
            return ;
        }
        if(reason.isEmpty() || urlImage.isEmpty()){

        }
        if(getOrder.getSchedule().getWorkDay().isAfter(LocalDate.now()) == false){
            System.out.println("yes this order is not after current date : "+ getOrder.getSchedule().getWorkDay());
            if(getOrder.getSchedule().getTimeSlot().getStartTime().isAfter(LocalTime.now()) == false){
                System.out.println("yes this order is not after current time : "+ getOrder.getSchedule().getTimeSlot().getStartTime());
                //logic here
                CancelRequest newRequest = CancelRequest.builder()
                        .status(CancelRequestStatus.PENDING)
                        .fileURL(urlImage)
                        .reason(reason)
                        .timeCancel(LocalDateTime.now())
                        .order(getOrder)
                        .worker(getWorker)
                        .build();
                System.out.println(newRequest);
            }
            System.out.println("time slot invalid");
        }else{
            System.out.println("date invalid");
        }

    }
    @Autowired
    private LeaveReportRepository leaveReportRepository;
    @Value("${mail.mailType.acceptLeaveReport}")
    private String LEAVE_REPORT;
    @Test
    void sendAcceptEmail(){
        Account getAccount = accountRepository.findByAccountId(54l);
        LeaveReport getReport = leaveReportRepository.findByLeaveReportId(2l);
        emailService.sendMailWithTemplate(getAccount,LEAVE_REPORT,getReport);
    }
    @Autowired
    private TransactionRepository transactionRepository;
    @Value("${mail.mailType.adminRefundTransaction}")
    private String ADMIN_REFUNDTRANSACTION_MAIL;
    @Test
    void sendRefundEmail(){
        Account getAccount = accountRepository.findByAccountId(54l);
        Transaction getTransaction = transactionRepository.findById(60l).get();
        emailService.sendMailWithTemplate(getAccount,ADMIN_REFUNDTRANSACTION_MAIL,getTransaction);
    }
    @Value("${mail.mailType.cancelRequest}")
    private String CANCEL_REQUEST;
    @Test
    void sendCancelRequestEmail(){
        CancelRequest getCancelRequest = cancelRequestRepository.findById(1l).get();
        emailService.sendMailWithTemplate(CANCEL_REQUEST,getCancelRequest);
    }
    @Autowired
    private OrderService orderService;
    @Value(value = "${order.policy.max_order_a_day}")
    private int orderMax;
    @Test
    void testBlockToomuchOrderInday(){
        long userId = 54;
        LocalDate localDate = LocalDate.now();
        LocalDateTime getStart = localDate.atStartOfDay();
        LocalDateTime getEnd = localDate.plusDays(1).atStartOfDay().minusSeconds(1);
        System.out.println(getStart + "    "+ getEnd);
        List<Order> getAllOrder = orderRepository.findAllByCreateTimeBetweenAndAccount_AccountId(getStart,getEnd,userId);
        System.out.println(getAllOrder.size());
        int size = getAllOrder.size();
        if(size > orderMax){
            System.out.println("off limit");
        }else{
            System.out.println("good");
        }
    }
}

