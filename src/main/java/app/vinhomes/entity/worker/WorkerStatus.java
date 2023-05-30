package app.vinhomes.entity.worker;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.order.ServiceCategory;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
            referencedColumnName = "account_id",
            nullable = false
    )
    @JsonBackReference
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "accountId")
    @JsonIdentityReference(alwaysAsId = true)
    private Account account;

    @ManyToOne //Job of worker
    @JoinColumn(
            name = "job",
            referencedColumnName = "servicecate_id",
            nullable = false
    )
    private ServiceCategory serviceCategory;

}
