package app.vinhomes.event.event_storage;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SendEmailAdminRefund {
    private Account account;
    private Transaction transaction;
}
