package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.type_enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //List<Order> findAllBySchedule_WorkDayAndSchedule_TimeSlotAndService_ServiceCategory(LocalDate workDay, TimeSlot timeSlot, ServiceCategory serviceCategory);

    List<Order> findAllByService_ServiceId(Long serviceId);

    List<Order> findAllByAccount(Account account);

    List<Order> findAllBySchedule_WorkDayAndSchedule_TimeSlotAndService_ServiceCategoryAndStatus(LocalDate workDay, TimeSlot timeSlot, ServiceCategory serviceCategory, OrderStatus status);
}
