package app.vinhomes.event.event_storage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SendSmsForgetAccount {
    public String phonenumberTo;
}
