package app.vinhomes;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ServerSetup_Teardown {
    @Autowired
    private CustomerSessionListener listener;
    @PreDestroy//before all resource is flush away , can be used to clear session data or do other stuff
    public void destroyServer(){
        listener.getActiveSessions().stream().forEach(session -> System.out.println(session.getId()));
        System.out.println("end server");
    }
    @PostConstruct// after all bean and service have been initialized, can be used to set basic config if you want
    public void initServer(){
        System.out.println("start server");
    }
}
