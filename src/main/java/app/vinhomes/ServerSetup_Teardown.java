package app.vinhomes;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class ServerSetup_Teardown {
    @PreDestroy//before all resource is flush away , can be used to clear session data or do other stuff
    public void destroyServer(){
        System.out.println("end server");
    }
    @PostConstruct// after all bean and service have been initialized, can be used to set basic config if you want
    public void initServer(){
        System.out.println("start server");
    }
}
