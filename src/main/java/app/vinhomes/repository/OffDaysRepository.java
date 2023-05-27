package app.vinhomes.repository;

import app.vinhomes.entity.worker.OffDays;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OffDaysRepository extends JpaRepository<OffDays, Long> {
    List<OffDays> findByOffDay(LocalDate offDay);
}
