package app.vinhomes.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //Use for API
@RequestMapping(value = "/api/logout")
public class LogoutHandler {
    @PostMapping()
    public ResponseEntity<String> logout(HttpServletRequest request) {
            // Return a success response
            HttpSession session = request.getSession(false);
            session.invalidate();
            return ResponseEntity.ok("Logout successful");
    }
}
