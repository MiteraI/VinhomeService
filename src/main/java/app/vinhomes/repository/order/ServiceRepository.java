package app.vinhomes.repository.order;

import app.vinhomes.entity.order.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    Service getServicesByServiceId(Long id);

    List<Service> findByServiceCategory_serviceCategoryId(Long servicecateId);
}
