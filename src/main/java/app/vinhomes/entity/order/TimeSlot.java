package app.vinhomes.entity.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Table(
        name = "tbl_timeslot"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timeslot_id")
    private Long timeSlotId;

    @Column(name = "start_time", nullable = false)
    @Basic
    @Temporal(TemporalType.TIME)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    @Basic
    @Temporal(TemporalType.TIME)
    private LocalTime endTime;
}
