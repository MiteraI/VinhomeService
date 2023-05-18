package app.vinhomes.repository;

import app.vinhomes.entity.order.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
