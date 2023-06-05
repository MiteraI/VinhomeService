package app.vinhomes.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //Use for API
//@RequestMapping(value = "/api/logout")
@RequestMapping(value = "/log/logout")
public class LogoutHandler {
    @PostMapping()
    public ResponseEntity<String> logout(HttpServletRequest request) {
        System.out.println("inside logout handler");
        SecurityContextHolder.clearContext();
        System.out.println(SecurityContextHolder.getContext());
        // Return a success response
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        for(Cookie cookie : request.getCookies()){
            System.out.println(cookie.getName() + "     " +cookie.getValue());
            cookie.setMaxAge(0);
        }


        return ResponseEntity.ok("Logout successful");
    }
}
