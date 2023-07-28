package app.vinhomes.security.config;

import app.vinhomes.CustomerSessionListener;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.customer.AddressRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import jakarta.servlet.ServletException;
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
import java.util.List;

@Component
public class SimpleSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private CustomerSessionListener customerSessionListener;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PhoneRepository phoneRepository;
    private SimpleUrlAuthenticationSuccessHandler pageController =
            new SimpleUrlAuthenticationSuccessHandler("/");
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        System.out.println("in on auth");
        System.out.println(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ( (UserDetails) principal). getUsername();
        } else {
            username = principal.toString();
        }
        Account account = accountRepository.findByAccountName(username);
        Address address = account.getAddress();
        List<Phone> fone = phoneRepository.findByAccountId(account.getAccountId());
        for (final GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            if (authorityName.equals("2")) {
                request.getSession().setAttribute("loginedUser",account);
                //customerSessionListener.
                this.pageController.onAuthenticationSuccess(request,response,authentication);
                return;
            } else if (authorityName.equals("1")) {
                request.getSession().setAttribute("loginedUser",account);
                request.getSession().setAttribute("phone", fone);
                this.pageController.onAuthenticationSuccess(request,response,authentication);
                return;
            }
        }
//        authentication.setAuthenticated(true);
        request.getSession().setAttribute("loginedUser",account);
        request.getSession().setAttribute("address", address);
        request.getSession().setAttribute("phone", fone);

        this.pageController.onAuthenticationSuccess(request,response,authentication);
    }
}