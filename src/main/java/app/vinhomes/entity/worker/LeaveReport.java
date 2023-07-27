package app.vinhomes.entity.worker;

import app.vinhomes.entity.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table (name = "tbl_leave_report")
public class LeaveReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leave_report_id")
    private Long leaveReportId;

    @ManyToOne(
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "worker_id",
            referencedColumnName = "account_id",
            nullable = false
    )
    private Account worker;

    private LocalDate startTime;
    private LocalDate endTime;
    @Lob
    @Column(
            length = 1024
    )
    private String reason;
    private String fileURL;

    @Column(columnDefinition = "integer default 0")
    private int status;


}
