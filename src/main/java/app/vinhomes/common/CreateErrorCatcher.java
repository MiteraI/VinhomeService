package app.vinhomes.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    private List<String> addressAddr;
    private String phoneNumberId;

    public CreateErrorCatcher(String usernameErr, String emailErr, String firstnameErr, String lastnameErr, String dateErr, List<String> addressAddr, String phonenumberErr) {
        this.usernameErr = usernameErr;
        this.emailErr = emailErr;
        this.firstnameErr = firstnameErr;
        this.lastnameErr = lastnameErr;
        this.dateErr = dateErr;
        this.addressAddr = addressAddr;
        this.phonenumberErr = phonenumberErr;
    }

    public CreateErrorCatcher(String usernameErr, String emailErr, String dateErr, String phonenumberErr) {
        this.usernameErr = usernameErr;
        this.emailErr = emailErr;
        this.dateErr = dateErr;
        this.phonenumberErr = phonenumberErr;
    }
    public CreateErrorCatcher(String usernameErr, String emailErr, String dateErr, String phonenumberErr, String phoneNumberId) {
        this.usernameErr = usernameErr;
        this.emailErr = emailErr;
        this.dateErr = dateErr;
        this.phonenumberErr = phonenumberErr;
        this.phoneNumberId = phoneNumberId;
    }

    public CreateErrorCatcher(String usernameErr, String passwordErr, String emailErr, String firstnameErr, String lastnameErr, String dateErr, String phonenumberErr, String addressErr) {
        this.usernameErr = usernameErr;
        this.passwordErr = passwordErr;
        this.emailErr = emailErr;
        this.firstnameErr = firstnameErr;
        this.lastnameErr = lastnameErr;
        this.dateErr = dateErr;
        this.phonenumberErr = phonenumberErr;
        this.addressErr = addressErr;
    }
}