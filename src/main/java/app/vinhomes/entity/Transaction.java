package app.vinhomes.entity;

import app.vinhomes.entity.type_enum.TransactionStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_transaction")
@Data
//@Getter
//@Setter
//@ToString
/////
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    private Long transactionId;
    private String paymentMethod;
    private String bankCode;
    private String vnpTxnRef;
    private long vnpTransactionDate;
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    @OneToOne(
            cascade = CascadeType.MERGE
    )
    @JoinColumn(
            name = "transaction_id"
    )
    @MapsId
    private Order order;
}
