package app.vinhomes.repository.worker;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.entity.worker.WorkerStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkerStatusRepository extends JpaRepository<WorkerStatus, Long> {
    List<WorkerStatus> findByServiceCategoryAndStatusOrderByWorkCountAsc(ServiceCategory serviceCategory, int status);

    List<WorkerStatus> findTop2ByServiceCategoryOrderByWorkCountAsc(ServiceCategory serviceCategory);

    WorkerStatus findByAccount(Account account);
}
