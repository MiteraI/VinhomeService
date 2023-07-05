package app.vinhomes.repository;

import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.repository.order.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleRepositoryTest {
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Test
    public void printAll() {
        System.out.println(scheduleRepository.findAllByWorkDayBetweenAndWorkers_AccountIdAndOrder_Status(
                LocalDate.parse("2023-06-26"),
                LocalDate.parse("2023-07-10"),
                2L,
                OrderStatus.CANCEL
        ));
    }

}