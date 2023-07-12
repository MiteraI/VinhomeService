package app.vinhomes.service;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.order.ScheduleRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private AccountRepository accountRepository;
    public Set<Account> getWorkersAccountMatchWorkday_TimeSlot(LocalDate workday,Long timeSlotID){
        List<Schedule> getSchedule = scheduleRepository.findAllByWorkDay(workday);
        //unique list of schedule found to be match that day
        Set<Account> getAccountWorkers = new LinkedHashSet<>();
        getSchedule.forEach(schedule -> {
            if(schedule.getTimeSlot().getTimeSlotId() == timeSlotID){
                //do nothing
            }else{
                List<Account> getWorkers = schedule.getWorkers();
                getWorkers.forEach(worker -> {
                    getAccountWorkers.add(worker);
                });
            }
        });
        System.out.println(getAccountWorkers);
        return getAccountWorkers;
    }
}
