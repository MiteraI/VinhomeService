package app.vinhomes.security.esms.otp_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPAttribute {
    private String oneTimePassword;
    private LocalDateTime expired;
}
