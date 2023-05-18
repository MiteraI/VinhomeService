package app.vinhomes.entity.order;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Column(name = "order_id")
    private Long scheduleId;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "work_time")
    private LocalDateTime startTime;

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "checkout_time")
    private LocalDateTime endTime;

    @OneToOne
    @JoinColumn(
            name = "order_id",
            referencedColumnName = "order_id"
    )
    @ToString.Exclude
    @JsonBackReference
    @MapsId
    private Order order;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tbl_worker_schedule",
            joinColumns = @JoinColumn(
                    name = "order_id",
                    referencedColumnName = "order_id"
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
