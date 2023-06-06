package app.vinhomes.Security.Config;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class SimpleSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
        private AccountRepository accountRepository;
    private SimpleUrlAuthenticationSuccessHandler pageController =
        new SimpleUrlAuthenticationSuccessHandler("/");
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        System.out.println("in on auth");
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
             username = ( (UserDetails) principal). getUsername();
        } else {
             username = principal.toString();
        }
        Account account = accountRepository.findByAccountName(username);
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("2")) {
                request.getSession().setAttribute("loginedUser",account);
                this.pageController.onAuthenticationSuccess(request,response,authentication);return;
            } else if (authorityName.equals("1")) {
                request.getSession().setAttribute("loginedUser",account);
                this.pageController.onAuthenticationSuccess(request,response,authentication);
                return;
            }
        }
        request.getSession().setAttribute("loginedUser",account);
        this.pageController.onAuthenticationSuccess(request,response,authentication);
    }
}
