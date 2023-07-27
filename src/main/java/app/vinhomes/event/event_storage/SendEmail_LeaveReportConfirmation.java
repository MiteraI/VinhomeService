package app.vinhomes.event.event_storage;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.worker.LeaveReport;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SendEmail_LeaveReportConfirmation {
    private Account account;
    private LeaveReport getReport;
}
