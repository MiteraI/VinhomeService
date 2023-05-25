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

}
