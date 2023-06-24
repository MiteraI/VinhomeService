package app.vinhomes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
@Getter
@AllArgsConstructor
public class triggerEvent {
    private String whatever;
    public String whateverAction(String whatever){
        System.out.println("take whatever: "+ whatever);
        return whatever +" received";
    }
    public void actionEvent(){
        System.out.println("yes run this function as an event triggerer");
    }
}
