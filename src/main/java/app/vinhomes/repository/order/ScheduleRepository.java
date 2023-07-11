package app.vinhomes.repository.order;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.Schedule;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.type_enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByWorkDayBetweenAndWorkers_AccountId(LocalDate startDate, LocalDate endDate, Long workerId);
    List<Schedule> findAllByWorkDayBetweenAndWorkers_AccountIdAndOrder_Status(LocalDate startDate, LocalDate endDate, Long workerId, OrderStatus status);
    Schedule findByOrder (Order order);
    List<Schedule> findAllByWorkDayNot(LocalDate workday);
    List<Schedule> findAllByWorkDay(LocalDate workday);
}
