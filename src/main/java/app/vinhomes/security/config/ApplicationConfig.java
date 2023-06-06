package app.vinhomes.security.config;


import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Configuration // this will pick up this class and try to implement and inject all the bean we
        // declared in this config
@RequiredArgsConstructor// this when we need to inject some beans
public class ApplicationConfig  {
    private final AccountRepository accountRepository;
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Optional<Account> account = accountRepository.findUsername(username);
                if(account != null){
                    System.out.println("IN USER DETAIL SERVICE,yes found in db");
                    return new User(account.get().getUsername(),account.get().getPassword(),roleToAuthority(account.get().getRole()));
                }
                else{
                    System.out.println("not in db");
                    return null;
                }
            }
        };
    }
    private Collection<GrantedAuthority> roleToAuthority(int role) {
        GrantedAuthority authority = new SimpleGrantedAuthority(String.valueOf(role));
        Collection<GrantedAuthority> authorities = Collections.singleton(authority);
        return authorities;
    }
    @Bean// from spring
    public AuthenticationProvider authenticationProvider(){ // data access object, fetcth user detail , password and stuff
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean// what type of encoder we want
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
