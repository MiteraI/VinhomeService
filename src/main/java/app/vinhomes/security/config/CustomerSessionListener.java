package app.vinhomes.security.config;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
//import org.springframework.security.web.session.HttpSessionEventPublisher;
//HttpSessionListener
@WebListener
public class CustomerSessionListener implements HttpSessionListener  {
    private int SESSION_TIMEOUT = 300;
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("session created");
        se.getSession().setMaxInactiveInterval(SESSION_TIMEOUT);
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("session destroyed");
        se.getSession().invalidate();
    }
}
