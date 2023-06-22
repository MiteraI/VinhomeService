package app.vinhomes.entity.worker;

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
    private Long workerStatusId;
    private LocalDate startTime;
    private LocalDate endTime;
    @Lob
    private String reason;
    private String fileURL;

    @Column(columnDefinition = "integer default 0")
    private int status;

    @OneToMany (
            mappedBy = "leaveReport",
            cascade = CascadeType.ALL
    )
    private List<Leave> leave;
}
