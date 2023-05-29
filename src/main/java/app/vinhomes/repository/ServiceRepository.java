package app.vinhomes.repository;

import app.vinhomes.entity.order.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Service getServicesByServiceId(Long id);
}
