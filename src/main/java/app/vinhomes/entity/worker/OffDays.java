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
        name = "tbl_off_days"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffDays {
    //Worker will have a function that
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "off_id")
    private Long offDaysId;

    @Temporal(TemporalType.DATE)
    private LocalDate offDay;

    @ManyToOne()
    @JoinColumn(
            name = "worker_id",
            referencedColumnName = "account_id"
    )
    private Account account;
}
