package app.vinhomes.repository;

import app.vinhomes.entity.PaymentCategory;
import app.vinhomes.entity.order.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentCategoryRepository paymentCategoryRepository;
    @Test
    public void addPayment() {
        PaymentCategory paymentCategory = paymentCategoryRepository.findById(1L).get();
        Payment payment = Payment.builder()
                .paymentName("MoMo")
                .paymentCategory(paymentCategory)
                .build();
        paymentCategory.addPayment(payment);
        paymentRepository.save(payment);
    }

    @Test
    public void deletePayment() {
        paymentRepository.deleteById(2L);
    }
    @Test
    public void printAllPayment() {
        System.out.println("Payments info = " + paymentRepository.findAll());
    }
}