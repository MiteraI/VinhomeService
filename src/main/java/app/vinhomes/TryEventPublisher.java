package app.vinhomes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TryEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher ;

    public void tryPublishEvent(String whatever){
        TryEventPublisher eventPublisher = new TryEventPublisher();
        System.out.println("inside event publisher");
        applicationEventPublisher.publishEvent(new triggerEvent(whatever));
    }
}
