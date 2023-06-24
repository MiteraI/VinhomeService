package app.vinhomes;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ListenerNo1 {
    @EventListener
    public void listeningNo1(triggerEvent event)
    {
        String whateverInformation = "random stuff pass through the event then action ";
        System.out.println("listener No1 -- before processing stuff");
        System.out.println("listener No1 --: "+ event.whateverAction(whateverInformation));
        System.out.println("listener No1 -- after processing stuff");
    }
}
