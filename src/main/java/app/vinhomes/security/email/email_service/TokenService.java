package app.vinhomes.security.email.email_service;

import app.vinhomes.security.email.email_dto.TokenEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {
    private long expriredTimeMili = 1000 * 60 * 2;

    public TokenEntity createTokenEntity(String email){
        String tokenvalue = generateTokenValue();
        Date exprired = new Date(System.currentTimeMillis()+ expriredTimeMili);
        return new TokenEntity(email,tokenvalue,exprired);
    }
    private String generateTokenValue(){
        String tokenvalue = UUID.randomUUID().toString();
        return tokenvalue;
    }
    private boolean checkIfExpired(TokenEntity token){
        return token.getExpired().before(new Date(System.currentTimeMillis()));
    }
    private boolean emailChecker(){
        return true;
    }
}
