package app.vinhomes;


import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@WebListener
@Configuration
public class CustomerSessionListener implements HttpSessionListener  {
    private static int SESSION_TIMEOUT = 200;
    private static final Map<String, HttpSession> SESSION_HASH_MAP = new HashMap<>();

    public List<HttpSession> getActiveSessions() {
        return new ArrayList<>(SESSION_HASH_MAP.values());
    }
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("session created");
        SESSION_HASH_MAP.put(se.getSession().getId(),se.getSession());
        getActiveSessions().stream().forEach(session -> System.out.println(session.getId()));
        se.getSession().setMaxInactiveInterval(SESSION_TIMEOUT);
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("session destroyed");
        SESSION_HASH_MAP.remove(se.getSession().getId(),se.getSession());
        getActiveSessions().stream().forEach(session -> System.out.println(session.getId()));
        se.getSession().invalidate();
    }


}
