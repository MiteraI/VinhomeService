package app.vinhomes.repository.order;

import app.vinhomes.entity.order.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByWorkDayBetweenAndWorkers_AccountId(LocalDate startDate, LocalDate endDate, Long workerId);

}