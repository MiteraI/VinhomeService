package app.vinhomes.entity.worker;

import app.vinhomes.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(
        name = "tbl_off_days"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OffDays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "off_id")
    private Long offDaysId;

    @Temporal(TemporalType.DATE)
    private Date offDay;

    @ManyToOne()
    @JoinColumn(
            name = "worker_id",
            referencedColumnName = "account_id"
    )
    private Account account;
}
