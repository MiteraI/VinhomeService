package app.vinhomes.security.config;

import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SimpleSuccessHandler successHandler;
    private final SimpleLogoutHandler simpleLogoutHandler;
    private final UserDetailsService userDetailsService;
    private final SimpleAuthenticationFailure simpleAuthenticationFailure;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/login").permitAll()
                )
                .formLogin(form -> form.loginPage("/login")
                        .failureHandler(simpleAuthenticationFailure)
                        .successHandler(successHandler)
                )
                .rememberMe(token -> token
                        .userDetailsService(userDetailsService)
                        .authenticationSuccessHandler(successHandler)
                        .tokenValiditySeconds(60 * 60)
                        .key("anythingyoulike")
                )
                .authorizeHttpRequests(auth -> auth.requestMatchers("/UserRestController/**").hasAuthority("2"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/order/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/worker/**").hasAuthority("1"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/admin/**").hasAuthority("2"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/admin").hasAuthority("2"))
                .authorizeHttpRequests(any -> any.anyRequest().permitAll())
                .logout(out -> out
                        .logoutUrl("/logout")
                        .addLogoutHandler(simpleLogoutHandler)
                        .addLogoutHandler(securityContextLogoutHandler())
                        .logoutSuccessHandler(simpleLogoutSuccesesHandler())
                        .invalidateHttpSession(true)
                        )
//                .logoutUrl("/api/logout")
        ;
        return httpSecurity.build();
    }
    @Bean
    public SimpleLogoutSuccesesHandler simpleLogoutSuccesesHandler() {
        return new SimpleLogoutSuccesesHandler();
    }
    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler(){
        return new SecurityContextLogoutHandler();
    }
    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> sessionListener() {
        return new ServletListenerRegistrationBean<HttpSessionListener>(new CustomerSessionListener());
    }
}
