package app.vinhomes.entity;

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
    private String vnp_txnRef;
    private long vnp_TransactionDate;
    @OneToOne(
            cascade = CascadeType.MERGE
    )
    @JoinColumn(
            name = "transaction_id"
    )
    @MapsId
    private Order order;
}
