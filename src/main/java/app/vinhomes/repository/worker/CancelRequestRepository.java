package app.vinhomes.repository.worker;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.worker.CancelRequest;
import app.vinhomes.entity.worker.Leave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CancelRequestRepository extends JpaRepository<CancelRequest, Long> {
    Optional<List<CancelRequest>> findByWorkerAndOrder(Account worker, Order order);
}
