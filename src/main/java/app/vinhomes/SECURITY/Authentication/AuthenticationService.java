package app.vinhomes.SECURITY.Authentication;


import app.vinhomes.SECURITY.Config.SimpleSuccessHandler;
import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
    /// this is where they get database infor and create the token from it
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SimpleSuccessHandler successHandler;

    public Account register(Account account) {
        System.out.println("inside register");
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        var savedAccount = accountRepository.save(account);
        return savedAccount;
    }


    public Account authenticate(AuthenticationRequest request) {
        /// if the username and password is not correct, an exception is throw
        System.out.println("before authenticated");
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        System.out.println("yes pass auth");

        SecurityContextHolder.getContext().setAuthentication(auth);
        var account = accountRepository.findByAccountName(request.getUsername());
        System.out.println();
        if (account == null) {
            System.out.println("NOT LEGIT LOGIN");
            return account;
        }

     return null;

    }


}
