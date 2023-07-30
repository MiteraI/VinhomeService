package app.vinhomes.security.config;

import app.vinhomes.CustomerSessionListener;
import jakarta.servlet.http.HttpSessionListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.COOKIES;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final SimpleSuccessHandler successHandler;
    private final SimpleLogoutHandler simpleLogoutHandler;
    private final UserDetailsService userDetailsService;
    private final SimpleAuthenticationFailure simpleAuthenticationFailure;
    private final AuthenticationEntry authenticationEntry;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .cors().and()
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

//                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/order/**").hasAnyAuthority("0", "1", "2"))
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/order/**").permitAll())
//                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/worker/**").hasAnyAuthority("1","2"))
//                //.authorizeHttpRequests(auth -> auth.requestMatchers("/api/order/getSession").permitAll())
//                .authorizeHttpRequests(auth -> auth.requestMatchers("/vnpay/createPayment").authenticated())
//                .authorizeHttpRequests(auth -> auth.requestMatchers("/vnpay/createPayment").hasAnyAuthority("0","2"))
//                .authorizeHttpRequests(auth -> auth.requestMatchers("/mail/**").permitAll())
//                .authorizeHttpRequests(auth -> auth.requestMatchers("/homepage").hasAuthority("0"))
//                .authorizeHttpRequests(auth -> auth.requestMatchers("/your-schedule").hasAnyAuthority("1","2"))
//                .authorizeHttpRequests(auth -> auth.requestMatchers("/leave-register").hasAnyAuthority("1","2"))
////                .authorizeHttpRequests(auth -> auth.requestMatchers("/category-services/**").hasAuthority("0"))
//                .authorizeHttpRequests(authUser -> {
//                    authUser.requestMatchers("/order-history").hasAnyAuthority("0","2");
//                    authUser.requestMatchers("/order-history/**").hasAnyAuthority("0","2");
//                    //authUser.requestMatchers("/service/**").hasAnyAuthority("0","2");
//                    authUser.requestMatchers("/verification").hasAnyAuthority("0","2");
//                    authUser.requestMatchers("/transaction/cancelOrder/refundTransaction/**").hasAnyAuthority("0","2");
//                    authUser.requestMatchers("/vnpay/createPayment").hasAnyAuthority("0","2");
//                    authUser.requestMatchers("/profile").hasAnyAuthority("0","2");
//                })
//                .authorizeHttpRequests(authAdmin ->{
//                    authAdmin.requestMatchers("/UserRestController/**").hasAuthority("2");
//                    authAdmin.requestMatchers("/api/admin/**").hasAuthority("2");
//                    authAdmin.requestMatchers("/admin").hasAuthority("2");
//                    authAdmin.requestMatchers("/adminDisplayWorker_page").hasAuthority("2");
//                    authAdmin.requestMatchers("/adminDisplayCustomer_page").hasAuthority("2");
//                    authAdmin.requestMatchers("/admin_UpdateWorker/**").hasAuthority("2");
//                    authAdmin.requestMatchers("/admin_UpdateCustomer/**").hasAuthority("2");
//                    authAdmin.requestMatchers("/see-leave-report").hasAuthority("2");
//                    authAdmin.requestMatchers("/see-all-order-by-admin").hasAuthority("2");
//                    authAdmin.requestMatchers("/see-all-services").hasAuthority("2");
//                    authAdmin.requestMatchers("/see-all-categories").hasAuthority("2");
//                    authAdmin.requestMatchers("/admin-order-detail/**").hasAuthority("2");
//                })
//                .authorizeHttpRequests(any -> any.anyRequest().permitAll())
                ///   neww security stuff
                //.authorizeHttpRequests(auth -> auth.requestMatchers("/api/order/**").hasAnyAuthority("0", "1", "2"))

                .authorizeHttpRequests(authWorker -> {
                    authWorker.requestMatchers("/api/worker/**").hasAnyAuthority("1","2");
                    authWorker.requestMatchers("/your-schedule").hasAnyAuthority("1","2");
                    authWorker.requestMatchers("/leave-register").hasAnyAuthority("1","2");
                })
                .authorizeHttpRequests(authUser -> {
                    authUser.requestMatchers("/order-history").hasAnyAuthority("0","2");
                    authUser.requestMatchers("/order-history/**").hasAnyAuthority("0","2");
                    authUser.requestMatchers("/verification").hasAnyAuthority("0","2");
                    authUser.requestMatchers("/transaction/cancelOrder/refundTransaction/**").hasAnyAuthority("0","2");
                    authUser.requestMatchers("/vnpay/createPayment").hasAnyAuthority("0","2");
                    authUser.requestMatchers("/profile").hasAnyAuthority("0","2");
                })
                .authorizeHttpRequests(authAdmin ->{
                    authAdmin.requestMatchers("/dashboard").hasAuthority("2");
                    authAdmin.requestMatchers("/UserRestController/**").hasAuthority("2");
                    authAdmin.requestMatchers("/api/admin/**").hasAuthority("2");
                    authAdmin.requestMatchers("/admin").hasAuthority("2");
                    authAdmin.requestMatchers("/adminDisplayWorker_page").hasAuthority("2");
                    authAdmin.requestMatchers("/adminDisplayCustomer_page").hasAuthority("2");
                    authAdmin.requestMatchers("/admin_UpdateWorker/**").hasAuthority("2");
                    authAdmin.requestMatchers("/admin_UpdateCustomer/**").hasAuthority("2");
                    authAdmin.requestMatchers("/see-leave-report").hasAuthority("2");
                    authAdmin.requestMatchers("/see-all-order-by-admin").hasAuthority("2");
                    authAdmin.requestMatchers("/see-all-services").hasAuthority("2");
                    authAdmin.requestMatchers("/see-all-categories").hasAuthority("2");
                    authAdmin.requestMatchers("/admin-order-detail/**").hasAuthority("2");
                })
                //.authorizeHttpRequests(any -> any.anyRequest().permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/resources/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/homepage").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/createAccountAPI/createAccountCustomer/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/static/**","/src/**","/template/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/login").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/logout").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/detail").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/category-services/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/service/{serviceId}").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/register").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/resetpwd").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/createAccountAPI/getAllPhonenumber").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/order/getSession").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/order/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/mail/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/accessDenied").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/forget_Account").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/createAccountAPI/forgetAccountMethod").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/services/**").permitAll())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/resetpassword/**").permitAll())

                .authorizeHttpRequests(any -> any.anyRequest().authenticated())


                .exceptionHandling().accessDeniedHandler(getAccessDeniedHandler()).and()
                .exceptionHandling().authenticationEntryPoint(authenticationEntry).and()

                .logout(out -> out
                        .logoutUrl("/logout")
                        .addLogoutHandler(simpleLogoutHandler)
                        .addLogoutHandler(securityContextLogoutHandler())
                        .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(COOKIES)))
                        .logoutSuccessHandler(simpleLogoutSuccesesHandler())
                        .invalidateHttpSession(true)
                )

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
    public AccessDeniedHandler getAccessDeniedHandler(){
        return new AccessDenied();
    }

}

