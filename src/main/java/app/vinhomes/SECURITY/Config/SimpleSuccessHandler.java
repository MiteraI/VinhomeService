package app.vinhomes.SECURITY.Config;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class SimpleSuccessHandler implements AuthenticationSuccessHandler {
//    SimpleUrlAuthenticationSuccessHandler userSuccessHandler =
//            new SimpleUrlAuthenticationSuccessHandler("/");
//    SimpleUrlAuthenticationSuccessHandler adminSuccessHandler =
//            new SimpleUrlAuthenticationSuccessHandler("/admin");
//    SimpleUrlAuthenticationSuccessHandler workSuccessHandler =
//            new SimpleUrlAuthenticationSuccessHandler("/worker");
    @Autowired
        private AccountRepository accountRepository;
    SimpleUrlAuthenticationSuccessHandler pageController =
        new SimpleUrlAuthenticationSuccessHandler("/");

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        System.out.println("in on auth");
        Cookie cookie = new Cookie("register","sadfkksf");
        cookie.setHttpOnly(false);
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
             username = ( (UserDetails) principal). getUsername();
            System.out.println(username);
        } else {
             username = principal.toString();
            System.out.println(username);
        }
        Account account = accountRepository.findByAccountName(username);
        System.out.println(account);
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("2")) {
                System.out.println("inside admin onAuth");
                response.addCookie(cookie);
                request.getSession().setAttribute("Authority",authorityName);
                request.getSession().setAttribute("loginedUser",account);
                this.pageController.onAuthenticationSuccess(request,response,authentication);return;
            } else if (authorityName.equals("1")) {
                System.out.println("inside worker onAuth");
                response.addCookie(cookie);
                request.getSession().setAttribute("Authority",authorityName);
                this.pageController.onAuthenticationSuccess(request,response,authentication);
                return;
            }
        }
        response.addCookie(cookie);
        this.pageController.onAuthenticationSuccess(request,response,authentication);
    }
}
