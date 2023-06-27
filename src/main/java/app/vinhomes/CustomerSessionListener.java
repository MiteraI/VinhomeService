package app.vinhomes;


import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//@WebListener
@Component
public class CustomerSessionListener implements HttpSessionListener  {
    private static int SESSION_TIMEOUT = 200;
    private static final Map<String, HttpSession> SESSION_HASH_MAP = new HashMap<>();

    public List<HttpSession> getActiveSessions() {
        SESSION_HASH_MAP.forEach((id,sessions) -> {
            System.out.println(id+"  "+sessions);
        });
        //return this.SESSION_HASH_MAP.forEach();
        return new ArrayList<>(SESSION_HASH_MAP.values());
    }
    public void destroyAllSession(){
        System.out.println("inside destroy all session");
        if(SESSION_HASH_MAP.isEmpty()){
            System.out.println("map emtpy, no need to destroy");
            return ;
        }
        SESSION_HASH_MAP.forEach((id,session)->{
            System.out.println(id+"  "+session);
            session.setMaxInactiveInterval(0);
            session.invalidate();
            SESSION_HASH_MAP.remove(id);
        });
    }
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        se.getSession().setMaxInactiveInterval(SESSION_TIMEOUT);
        System.out.println("session created");
        SESSION_HASH_MAP.put(se.getSession().getId(),se.getSession());
        //getActiveSessions();
    }
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("session destroyed");
        SESSION_HASH_MAP.remove(se.getSession().getId());
        se.getSession().invalidate();
    }
}
