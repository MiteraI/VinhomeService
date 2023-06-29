package app.vinhomes.event.event_storage;

import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalUnit;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class StartOrderCountDown {
    private LocalDateTime confirmOrderTime;
    private Long transactionId;
    public long getSecondTimeLeft(){
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = getConfirmOrderTime();
        Duration diff = Duration.between(from,to).abs();
        return diff.getSeconds();
    }
}
