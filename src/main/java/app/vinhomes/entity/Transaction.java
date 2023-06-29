package app.vinhomes.entity;


import app.vinhomes.entity.type_enum.TransactionStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "tbl_transaction")
@Data

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
