package app.vinhomes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateErrorCatcher {
    private String usernameErr;
    private String passwordErr;
    private String emailErr;
    private String firstnameErr;
    private String lastnameErr;
    private String dateErr;
    private String phonenumberErr;
    private String addressErr;

    public CreateErrorCatcher(String usernameErr, String passwordErr, String emailErr, String firstnameErr, String lastnameErr) {
        this.usernameErr = usernameErr;
        this.passwordErr = passwordErr;
        this.emailErr = emailErr;
        this.firstnameErr = firstnameErr;
        this.lastnameErr = lastnameErr;
    }
}


