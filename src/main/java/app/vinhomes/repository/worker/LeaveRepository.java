package app.vinhomes.repository.worker;

import app.vinhomes.entity.worker.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByLeaveDay(LocalDate offDay);
    List<Leave> findByAccount_AccountId(Long id);
}
