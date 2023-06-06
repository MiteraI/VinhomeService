package app.vinhomes.repository.order;

import app.vinhomes.entity.order.PaymentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCategoryRepository extends JpaRepository<PaymentCategory,Long> {
    PaymentCategory findByPaymentCategoryId(Long id);
}
