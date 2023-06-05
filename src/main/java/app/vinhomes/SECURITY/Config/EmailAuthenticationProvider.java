package app.vinhomes.SECURITY.Config;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
//implements AuthenticationProvider
public class EmailAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private  AccountRepository accountRepository;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        Account account = accountRepository.findByEmailEquals(email);
        if(account != null){
            if(passwordEncoder.matches(password,account.getPassword())){
                return new UsernamePasswordAuthenticationToken(
                        email,
                        password,
                        account.getAuthorities()
                );
            }
            else{
                throw new BadCredentialsException("invalid password or wrong email");

            }
        }else{
            throw new UsernameNotFoundException("email not valid");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
