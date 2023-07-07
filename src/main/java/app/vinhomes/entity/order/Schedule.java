package app.vinhomes.entity.order;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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

    @Column(name = "work_day")
    @Basic
    @Temporal(TemporalType.DATE)
    private LocalDate workDay;

    @ManyToOne(
            cascade = CascadeType.MERGE,
            fetch = FetchType.EAGER
    )
    @JoinColumn(
            name = "timeslot_id",
            referencedColumnName = "timeslot_id",
            nullable = false
    )
    private TimeSlot timeSlot;

    @OneToOne
    @JoinColumn(
            name = "order_id",
            referencedColumnName = "order_id"
    )
    @ToString.Exclude
    @JsonIgnoreProperties({
            "schedule","rating","comment"
    })
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
    @JsonIgnoreProperties(
            {
                    "email", "address", "phone", "password",
                    "accountName", "cookie", "dob", "accountStatus",
                    "role"
            }
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
