package app.vinhomes;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ListenerNo2 {

    @EventListener
    public void listenerNo2(triggerEvent event)
    {
        System.out.println("listenterNo2 -- before processing stuff");
        try {
            Thread.sleep(3000);
            System.out.println("inside listener no2 : "+event.getWhatever());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("listenterNo2 -- after processing stuff");
    }
}
