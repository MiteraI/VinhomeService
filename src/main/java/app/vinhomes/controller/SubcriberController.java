package app.vinhomes.controller;

import com.azure.core.annotation.Get;
import com.azure.core.annotation.Post;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//@RestController
@RequestMapping(value = "/sse")
public class SubcriberController {
    private long sseTimeout = 60000;
    public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();/// thread safe list
    @CrossOrigin
    @GetMapping(value = "/subcribe", consumes = MediaType.ALL_VALUE)
    public SseEmitter subcribe() {
        System.out.println("inside subcription api");
        SseEmitter sseEmitter = new SseEmitter(sseTimeout);
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        sseEmitter.onCompletion( () -> emitters.remove(sseEmitter));
        emitters.add(sseEmitter);
        System.out.println(sseEmitter);
        return sseEmitter;
    }

    @PostMapping(value = "/dispatchEvent",consumes = MediaType.ALL_VALUE)
    public void dispatchEventToSubcriber(@RequestParam String news){
        System.out.println("inside dispatcher event");
        for(SseEmitter emitter : emitters){
            System.out.println(emitter.toString());
            try {
                emitter.send(SseEmitter.event().name("serverNameEvent").data(news));
            } catch (IOException e) {
                System.out.println(e.getMessage());
                emitters.remove(emitter);
            }
        }
    }
}
