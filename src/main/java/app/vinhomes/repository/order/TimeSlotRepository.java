package app.vinhomes.repository.order;

import app.vinhomes.entity.order.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    TimeSlot findByTimeSlotId (Long timeSlotId);
}
