package app.vinhomes.repository.worker;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.order.ServiceCategory;
import app.vinhomes.entity.worker.WorkerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkerStatusRepository extends JpaRepository<WorkerStatus, Long> {
    List<WorkerStatus> findByServiceCategoryAndStatusOrderByWorkCountAsc(ServiceCategory serviceCategory, int status);

    List<WorkerStatus> findTop2ByServiceCategoryOrderByWorkCountAsc(ServiceCategory serviceCategory);
    WorkerStatus findByAccount_AccountId(Long accountId);
    WorkerStatus findByAccount(Account account);
    @Query(value = "select * from tbl_worker_status a where a.worker_id = ?1", nativeQuery = true)
    WorkerStatus findWorkerStatusById(long accountId);

}
