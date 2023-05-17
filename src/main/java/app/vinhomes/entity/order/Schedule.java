package app.vinhomes.entity.order;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(
        name = "tbl_schedule"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "work_time")
    private Date startTime;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "checkout_time")
    private Date endTime;

    @OneToOne
    @JoinColumn(
            name = "schedule_id",
            referencedColumnName = "order_id"
    )
    @MapsId
    private Order order;

    @ManyToMany
    @JoinTable(
            name = "tbl_worker_schedule",
            joinColumns = @JoinColumn(
                    name = "schedule_id",
                    referencedColumnName = "schedule_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "account_worker_id",
                    referencedColumnName = "account_id"
            )
    )
    private List<Account> workers;

    public void addWorker(Account worker) {
        if(workers == null) {
            workers = new ArrayList<>();
            workers.add(worker);
        } else {
            workers.add(worker);
        }
    }
}
