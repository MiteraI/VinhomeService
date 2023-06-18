package app.vinhomes.security.email.email_dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenEntity {
    private String email;
    private String tokenvalue;
    private Date expired;
}
