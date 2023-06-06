package app.vinhomes.security.authentication;


import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {
    /// this is where they get database infor and create the token from it
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Account register(Account account) {
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        var savedAccount = accountRepository.save(account);
        return savedAccount;
    }


    public Account authenticate(AuthenticationRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        var account = accountRepository.findByAccountName(request.getUsername());
        System.out.println();
        if (account == null) {
            return account;
        }

     return null;

    }


}
