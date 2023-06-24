package app.vinhomes.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class SubcriberController {
    public List<SseEmitter> emitters = new CopyOnWriteArrayList<>();/// thread safe list
    @CrossOrigin
    @RequestMapping(value = "/subcribe", consumes = MediaType.ALL_VALUE)
    public SseEmitter subcribe() {
        SseEmitter sseEmitter = new SseEmitter();
        try {
            sseEmitter.send(SseEmitter.event().name("INIT"));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        emitters.add(sseEmitter);
        return sseEmitter;
    }
}
