//package app.vinhomes.repository;
//
//import app.vinhomes.entity.Account;
//import app.vinhomes.entity.Order;
//import app.vinhomes.entity.order.Schedule;
//import app.vinhomes.entity.order.Service;
//import app.vinhomes.entity.order.TimeSlot;
//import app.vinhomes.entity.worker.WorkerStatus;
//import app.vinhomes.repository.order.PaymentRepository;
//import app.vinhomes.repository.order.ServiceCategoryRepository;
//import app.vinhomes.repository.order.ServiceRepository;
//import app.vinhomes.repository.order.TimeSlotRepository;
//import app.vinhomes.repository.worker.WorkerStatusRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//class OrderRepositoryTest {
//
//    @Autowired
//    private OrderRepository orderRepository;
//    @Autowired
//    private AccountRepository accountRepository;
//    @Autowired
//    private ServiceRepository serviceRepository;
//    @Autowired
//    private ServiceCategoryRepository serviceCategoryRepository;
//    @Autowired
//    private PaymentRepository paymentRepository;
//    @Autowired
//    private WorkerStatusRepository workerStatusRepository;
//    @Autowired
//    private TimeSlotRepository timeSlotRepository;
//
//    @Test
//    @Deprecated //Leave it to lam nguoi
//    public void addOrderThatAssignWorker() {
//        Account account = accountRepository.findById(1L).get();
//        Service service = serviceRepository.findById(3L).get();
//        Schedule schedule = Schedule.builder()
//                .workDay(LocalDate.now())
//                .build();
//        Order order = Order.builder()
//                .createTime(LocalDateTime.now().withNano(0))
//                .price(600000)
//                .account(account)
//                .service(service)
//                .payment(paymentRepository.findById(1L).get())
//                .schedule(schedule)
//                .build();
//        schedule.setOrder(order);
//        if (service.getNumOfPeople() == 2) {
//            List<WorkerStatus> workerStatuses = workerStatusRepository
//                    .findByServiceCategoryAndStatusOrderByWorkCountAsc(
//                            service.getServiceCategory(), 0);
//            for (WorkerStatus workerStatus : workerStatuses) {
//                schedule.addWorker(workerStatus.getAccount());
//                System.out.println(schedule);
//            }
//        }
//        orderRepository.save(order);
//    }
//
//    @Test
//    public void officialOrderThatAssignWorkerBasedOnSchedule() {
//        Account account = accountRepository.findById(1L).get();
//        //Assigning service
//        Service service = serviceRepository.findById(3L).get();
//        //Assigning timeslot
//        TimeSlot timeSlot = timeSlotRepository.findById(3L).get();
//        //Assigning schedule
//        Schedule schedule = Schedule.builder()
//                .workDay(LocalDate.of(2023, 5, 23))
//                .timeSlot(timeSlot)
//                .build();
//        //Initialize list of appropriate workers statuses for find worker account
//        List<WorkerStatus> workerStatuses = workerStatusRepository.findByServiceCategoryAndStatusOrderByWorkCountAsc(
//                serviceCategoryRepository.findById(service.getServiceCategory().getServiceCategoryId()).get()
//                , 0
//        );
//        //Transfer to worker account to list
//        List<Account> workerAccounts = new ArrayList<>();
//        for (WorkerStatus workerStatus : workerStatuses) {
//            workerAccounts.add(workerStatus.getAccount());
//        }
//
//        //Get order that is in the work day and timeslot the user chose with the job cate to find busy worker
//        List<Order> orders = orderRepository.
//                findAllBySchedule_WorkDayAndSchedule_TimeSlotAndService_ServiceCategory(
//                        schedule.getWorkDay()
//                        , timeSlot
//                        //Find serviceCate / job to know the workers
//                        , serviceCategoryRepository.findById(service.getServiceCategory().getServiceCategoryId()).get()
//                );
//
//        //Get busy worker list to exclude from the worker account list -> free workers ready to be assigned
//        List<Account> busyWorkerAccounts = new ArrayList<>();
//        for (Order order : orders) {
//            busyWorkerAccounts.addAll(order.getSchedule().getWorkers());
//        }
//
//        //Get free worker list from the 2 other list
//        List<Account> freeWorkerAccounts = new ArrayList<>();
//        if (busyWorkerAccounts.size() == 0) {
//            freeWorkerAccounts = workerAccounts;
//        } else {
//            for (Account worker : workerAccounts) {
//                boolean isBusy = false;
//                for (Account busyWorker : busyWorkerAccounts) {
//                    if (busyWorker.getAccountId() == worker.getAccountId()) {
//                        isBusy = true;
//                        break;
//                    }
//                }
//                if (!isBusy) {
//                    freeWorkerAccounts.add(worker);
//                }
//            }
//        }
//        try {
//            switch (service.getNumOfPeople()) {
//                case 1 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 1));
//                case 2 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 2));
//                case 3 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 3));
//                case 4 -> schedule.setWorkers(freeWorkerAccounts.subList(0, 4));
//            }
//        } catch (Exception ex) {
//            System.out.println("Not enough worker");
//        }
//        Order order = Order.builder()
//                .createTime(LocalDateTime.now().withNano(0))
//                .price(service.getPrice())
//                .account(account)
//                .service(service)
//                .payment(paymentRepository.findById(1L).get())
//                .schedule(schedule)
//                .build();
//        schedule.setOrder(order);
//
//        orderRepository.save(order);
//    }
//
//    @Test
//    public void printAllOrder() {
//        System.out.println("Orders info = " + orderRepository.findAll());
//    }
//
//    @Test
//    public void printAllOrderThatHasASpecificDayAndTimeSlot() {
//        Service service = serviceRepository.findById(2l).get();
//        List<Order> orders = orderRepository.findAllBySchedule_WorkDayAndSchedule_TimeSlotAndService_ServiceCategory(
//                LocalDate.of(2023, 5, 23)
//                , timeSlotRepository.findById(2L).get()
//                , serviceCategoryRepository.findById(
//                        service.getServiceCategory().getServiceCategoryId()).get()
//        );
//        System.out.println("Order list = " + orders);
//    }
//
//    @Test
//    public void deleteAll() {
//        orderRepository.deleteAll();
//    }
//}