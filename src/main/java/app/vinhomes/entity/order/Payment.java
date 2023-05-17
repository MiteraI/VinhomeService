package app.vinhomes.entity.order;

import app.vinhomes.entity.PaymentCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "tbl_payment"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "payment_name")
    private String paymentName;

    @ManyToOne(optional = false)
    @JoinColumn(
            name = "paymentcate_id",
            referencedColumnName = "paymentcate_id"
    )
    private PaymentCategory paymentCategory;
}
