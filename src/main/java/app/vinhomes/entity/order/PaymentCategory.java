package app.vinhomes.entity.order;

import app.vinhomes.entity.order.Payment;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "tbl_payment_category"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paymentcate_id")
    private Long paymentCategoryId;

    @Column(name = "paymentcate_name", nullable = false)
    private String paymentCategoryName;

    @OneToMany(
            mappedBy = "paymentCategory",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @Column(nullable = false)
    @JsonManagedReference
    private List<Payment> payments;
    public void addPayment(Payment payment) {
        if(payments == null) {
            payments = new ArrayList<>();
            payments.add(payment);
        } else payments.add(payment);
    }
}
