package app.vinhomes.SECURITY.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final SimpleSuccessHandler successHandler;
    private final EmailAuthenticationProvider emailAuthenticationProvider;
    private final SimpleLogoutHandler simpleLogoutHandler;
    private final UserDetailsService userDetailsService;
    private final SimpleAuthenticationFailure simpleAuthenticationFailure;
//        private final SimpleLogoutSuccesesHandler simpleLogoutSuccesesHandler;


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
                .rememberMe(token -> token.userDetailsService(userDetailsService)
                        .authenticationSuccessHandler(successHandler)
                        .tokenValiditySeconds(60 * 60)
                        .key("anythingyoulike")
                )
                .authorizeHttpRequests(auth -> auth.requestMatchers("/UserRestController/**").hasAuthority("2"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/worker").hasAuthority("1"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/admin").hasAuthority("2"))
                .authorizeHttpRequests(any -> any.anyRequest().permitAll())
                .logout(out -> out.logoutUrl("/api/logout")
                        .addLogoutHandler(simpleLogoutHandler)
                        .logoutSuccessHandler(simpleLogoutSuccesesHandler())
                        .invalidateHttpSession(true))
//                        .logoutSuccessHandler((request, response, authentication)
//                        -> SecurityContextHolder.clearContext()))                   // every time we call to this api, it will call logout handler
        ;
        return httpSecurity.build();

    }

    @Bean
    public SimpleLogoutSuccesesHandler simpleLogoutSuccesesHandler() {
        return new SimpleLogoutSuccesesHandler();
    }


}
