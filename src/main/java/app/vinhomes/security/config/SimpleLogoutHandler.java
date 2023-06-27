package app.vinhomes.security.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Enumeration;

@Service
@RequiredArgsConstructor
public class SimpleLogoutHandler implements org.springframework.security.web.authentication.logout.LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        System.out.println("inside logoutHandler");
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
//            Enumeration<String> sessionAtt = session.getAttributeNames();
//            while(sessionAtt != null && sessionAtt.nextElement().isBlank() ==false){
//                session.removeAttribute(sessionAtt.);
//            }
        }
        Cookie[] cookieList = request.getCookies();
        for(Cookie cookie :cookieList ){
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
    }
}
