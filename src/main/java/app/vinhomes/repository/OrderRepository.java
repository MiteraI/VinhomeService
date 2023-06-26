package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.entity.order.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllBySchedule_WorkDayAndSchedule_TimeSlotAndService_ServiceCategory(LocalDate workDay, TimeSlot timeSlot, ServiceCategory serviceCategory);

    List<Order> findAllByService_ServiceId(Long serviceId);

    List<Order> findAllByAccount(Account account);

    List<Order> findAllByAccount_AccountId(Long accountId);

    Order findByAccount_AccountIdAndOrderId(Long accountId, Long orderId);
}
