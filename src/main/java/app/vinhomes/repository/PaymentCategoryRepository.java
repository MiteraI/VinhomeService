package app.vinhomes.repository;

import app.vinhomes.entity.PaymentCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCategoryRepository extends JpaRepository<PaymentCategory,Long> {
    PaymentCategory findByPaymentCategoryId(Long id);
}
