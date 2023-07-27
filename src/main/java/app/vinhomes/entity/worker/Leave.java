package app.vinhomes.entity.worker;

import app.vinhomes.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "tbl_leave"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Leave {
    //Worker will have a function that
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_id")
    private Long leaveId;

    @Temporal(TemporalType.DATE)
    @Column(name = "leave_day")
    private LocalDate leaveDay;

    @ManyToOne()
    @JoinColumn(
            name = "worker_id",
            referencedColumnName = "account_id"
    )
    private Account account;

}
