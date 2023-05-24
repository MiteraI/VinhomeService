package app.vinhomes.repository;

import app.vinhomes.entity.ServiceCategory;
import app.vinhomes.entity.worker.WorkerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerStatusRepository extends JpaRepository<WorkerStatus, Long> {
    List<WorkerStatus> findByServiceCategoryAndStatusOrderByWorkCountAsc(ServiceCategory serviceCategory, int status);
}
