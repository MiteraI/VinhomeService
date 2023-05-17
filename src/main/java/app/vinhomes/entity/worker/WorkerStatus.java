package app.vinhomes.entity.worker;

import app.vinhomes.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "tbl_worker_status"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long workerStatusId;

    private int status;

    private int allowedDayOff;

    private int workCount;

    @OneToOne
    @JoinColumn(
            name = "worker_id",
            referencedColumnName = "account_id"
    )
    private Account account;


}
