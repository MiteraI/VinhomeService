package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.entity.order.TimeSlot;
import app.vinhomes.entity.type_enum.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    //List<Order> findAllBySchedule_WorkDayAndSchedule_TimeSlotAndService_ServiceCategory(LocalDate workDay, TimeSlot timeSlot, ServiceCategory serviceCategory);

    List<Order> findAllByService_ServiceId(Long serviceId);

    List<Order> findAllByAccount(Account account);

    List<Order> findAllBySchedule_WorkDayAndSchedule_TimeSlotAndService_ServiceCategoryAndStatus(LocalDate workDay, TimeSlot timeSlot, ServiceCategory serviceCategory, OrderStatus status);

    Order findByAccount_AccountIdAndOrderId(Long accountId, Long orderId);

    @Query(nativeQuery = true, value = "SELECT DBO.COUNT_RATING_FOR_SERVICE(:serviceId, :rating)")
    int COUNT_RATING_FOR_SERVICE(@Param("serviceId") Integer service_id, @Param("rating")Integer rating);

    Order findByOrderId (Long orderId);

    List<Order> findAllByCreateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findAllByCreateTimeBetweenAndStatus(LocalDateTime start, LocalDateTime end, OrderStatus status);
    List<Order> findAllByCreateTimeBetweenAndAccount_AccountId(LocalDateTime start, LocalDateTime end, Long accountId);
}
