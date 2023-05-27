package app.vinhomes.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class OffDaysRepositoryTest {
    @Autowired
    private OffDaysRepository offDaysRepository;
    @Test
    public void printAll() {
        System.out.println(offDaysRepository.findByOffDay(LocalDate.parse("2023-05-27")));
    }
}