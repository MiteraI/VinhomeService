package app.vinhomes.security.esms.otp_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetDTO {
    private String phoneNumber;
    private String oneTimePassword;
}
