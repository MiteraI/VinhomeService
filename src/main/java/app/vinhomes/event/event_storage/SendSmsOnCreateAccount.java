package app.vinhomes.event.event_storage;

import app.vinhomes.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendSmsOnCreateAccount {
    private Account FreshAccount;
}
