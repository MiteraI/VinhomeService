package app.vinhomes.entity.worker;



import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.type_enum.CancelRequestStatus;
import app.vinhomes.entity.type_enum.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_cancel_request")
public class CancelRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cancel_request_id")
    private Long cancelRequestId;

    @ManyToOne(
            //cascade = CascadeType.ALL
            //fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "worker_id",
            referencedColumnName = "account_id",
            nullable = false
    )
    private Account worker;

    private LocalDateTime timeCancel;
    @Lob
    @Column(
            length = 1024
    )
    private String reason;
    private String fileURL;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CancelRequestStatus status;

    @ManyToOne(
            //cascade = CascadeType.ALL
            //fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "id_order",
            referencedColumnName = "order_id",
            nullable = false
    )
    private Order order;

    @Override
    public String toString() {
        return "id: "+cancelRequestId+ " time: "+ timeCancel+ " workerName: "+ worker.getAccountName() + " Order: " + order;
    }

}
