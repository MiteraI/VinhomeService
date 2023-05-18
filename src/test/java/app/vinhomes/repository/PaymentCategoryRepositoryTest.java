package app.vinhomes.repository;

import app.vinhomes.entity.PaymentCategory;
import app.vinhomes.entity.order.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PaymentCategoryRepositoryTest {

    @Autowired
    private PaymentCategoryRepository paymentCategoryRepository;
    @Test
    public void addPaymentCategoryAlongWithPayments() {
        Payment payment1 = Payment.builder()
                .paymentName("VIB")
                .build();
        PaymentCategory paymentCategory = PaymentCategory.builder()
                .paymentCategoryName("Card")
                .payments(List.of(payment1))
                .build();
        payment1.setPaymentCategory(paymentCategory);
        paymentCategoryRepository.save(paymentCategory);
    }
}