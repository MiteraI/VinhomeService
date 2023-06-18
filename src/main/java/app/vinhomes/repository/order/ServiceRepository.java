package app.vinhomes.repository.order;

import app.vinhomes.entity.order.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Service getServicesByServiceId(long id);
}
