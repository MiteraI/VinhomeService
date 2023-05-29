package app.vinhomes.controller;


import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //Use for API
@RequestMapping(value = "/api/login")
public class AuthorizationAPI {
    @Autowired
    private AccountRepository accountRepository;
    @PostMapping()
    public ResponseEntity<String> login(@RequestBody JsonNode loginRequest, HttpServletRequest request, Model model) {
        String username = loginRequest.get("username").asText();
        String password = loginRequest.get("password").asText();
        Account account = accountRepository.findByAccountNameAndPassword(username,password);
        if (account != null) {
            // Return a success response
            HttpSession session = request.getSession();
            session.setAttribute("loginedUser", account);
            System.out.println(session.getAttribute("loginedUser"));
//            model.addAttribute("loggedUser", account);
            return ResponseEntity.ok("Login successful");
        } else {
            // Return an error response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
