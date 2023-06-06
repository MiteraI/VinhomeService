package app.vinhomes.entity.order;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;


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

    @Column(
            name = "payment_name",
            nullable = false
    )
    private String paymentName;

    @ManyToOne(
            optional = false, cascade = CascadeType.MERGE,
            fetch = FetchType.LAZY
    )
    @ToString.Exclude
    @JsonBackReference
    @JoinColumn(
            name = "paymentcate_id",
            referencedColumnName = "paymentcate_id",
            nullable = false
    )
    private PaymentCategory paymentCategory;

}
