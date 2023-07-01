package app.vinhomes.service;

import app.vinhomes.common.SessionUserCaller;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.order.ScheduleRepository;
import app.vinhomes.repository.order.TimeSlotRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkerService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    public List<Schedule> getSchedulesForSelf(LocalDate startDate, LocalDate endDate, HttpServletRequest request) {
        Account account = SessionUserCaller.getSessionUser(request);
        if (account == null) return new ArrayList<Schedule>();
        System.out.println(startDate);
        System.out.println(endDate);
        return scheduleRepository.findAllByWorkDayBetweenAndWorkers_AccountId(
                startDate
                , endDate
                , account.getAccountId()
        );
    }

    //Ham nay de clay worker nao la cho order nao de confirm
    public Account getWorkerOfOneOrderForConfirmation (Long orderId) {
        Account account = null;
        Order order = orderRepository.findByOrderId(orderId);
        Schedule schedule = scheduleRepository.findByOrder(order);
        List<Account> workers = schedule.getWorkers();
        if (workers != null) {
            for (Account worker: workers) {
                account = worker;
                return account;
            }
        }
        return null;
    }

    public boolean confirmOrder (Long workerId, Long orderId) {
        Account worker = accountRepository.findByAccountId(workerId);
        Order order = orderRepository.findByOrderId(orderId);
        Schedule schedule = scheduleRepository.findByOrder(order);
        LocalDate workDate = schedule.getWorkDay();
        TimeSlot timeSlot = schedule.getTimeSlot();
        if (workDate.isEqual(LocalDate.now()) && timeSlot.getStartTime().isBefore(LocalTime.now())) {
            order.setStatus(OrderStatus.valueOf("SUCCESS"));
            orderRepository.save(order);
            return true;
        }
        else {
            return false;
        }
    }
}
