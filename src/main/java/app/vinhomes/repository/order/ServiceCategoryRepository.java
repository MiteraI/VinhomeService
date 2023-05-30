package app.vinhomes.repository.order;

import app.vinhomes.entity.order.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository extends JpaRepository<ServiceCategory, Long> {
    ServiceCategory findByServiceCategoryId(Long Id);
}
