package app.vinhomes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "paymentcate_name")
    private String paymentCategoryName;
}
