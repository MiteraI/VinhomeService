package app.vinhomes.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthenticationEntry implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException, IOException {
        System.out.println("inside authentication entry point");
        System.out.println(SecurityContextHolder.getContext());
        //response.sendError(HttpServletResponse.SC_FORBIDDEN,"error");
        //response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.sendRedirect("/login");
    }
}
