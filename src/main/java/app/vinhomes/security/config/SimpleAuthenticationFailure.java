package app.vinhomes.security.config;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class SimpleAuthenticationFailure extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private AccountRepository accountRepository;


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        Account account = accountRepository.findByAccountName(username);
        StringBuilder message= new StringBuilder();
        if(account != null){
            message.append("=Incorrect Username Or Password");
        }
        else{
            message.append("=Not Found Username");
        }
        super.setDefaultFailureUrl("/login?error"+message.toString());
        super.onAuthenticationFailure(request, response, exception);
    }
}
