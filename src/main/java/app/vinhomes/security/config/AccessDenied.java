package app.vinhomes.security.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class AccessDenied implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        SecurityContext getContext = SecurityContextHolder.getContext();
        if(getContext != null){
            Authentication getAuthentication = getContext.getAuthentication();
            if(getAuthentication != null){
                System.out.println("this user is not allowed to access this endpoint at : "+ request.getRequestURI());
            }else{
                System.out.println("this user is not loggin yet");
            }
        }else{
            System.out.println("cannot find security context for this user");
        }
        response.sendRedirect("/accessDenied");

    }
}

