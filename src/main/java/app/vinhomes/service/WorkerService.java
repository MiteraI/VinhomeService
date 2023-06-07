package app.vinhomes.service;

import app.vinhomes.common.SessionUserCaller;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.repository.order.ScheduleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkerService {
    @Autowired
    private ScheduleRepository scheduleRepository;


    public List<Schedule> getSchedulesForSelf(LocalDate startDate, LocalDate endDate, HttpServletRequest request) {
        Account account = SessionUserCaller.getSessionUser(request);
        if (account == null) return new ArrayList<Schedule>();
        return scheduleRepository.findAllByDateBetweenAndAccount_AccountId(
                LocalDate.of(2023,5,1)
                , LocalDate.of(2023, 6, 30)
                , 5L
        );
    }
}
