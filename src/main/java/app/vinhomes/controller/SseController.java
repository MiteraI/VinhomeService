package app.vinhomes.controller;

import app.vinhomes.event.SSEEmitterWithStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@RestController
public class SseController {
    private final List<SSEEmitterWithStatus> emitters = new ArrayList<>();
    int unreadCount = 0;

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribeToEvents() {
        SSEEmitterWithStatus emitter = new SSEEmitterWithStatus();
        emitters.add(emitter);
        // Remove the emitter when it is completed
        emitter.onCompletion(() -> emitters.remove(emitter));
        return emitter;
    }

    // Method to send SSE events to all connected clients
    public void sendSSEEvent(String eventName, Object eventData) {
        List<SSEEmitterWithStatus> completedEmitters = new ArrayList<>();
        for (SSEEmitterWithStatus emitter : emitters) {
            System.out.println("i");
            try {
                emitter.send(SseEmitter.event().name(eventName).data(eventData));
                if (!emitter.isRead()) {
                    unreadCount++;
                }
                else {
                    unreadCount = 0;
                }
            } catch (Exception e) {
                // If there is an error sending the event, complete the emitter
                emitter.completeWithError(e);
                completedEmitters.add(emitter);
            }
        }

        // Remove completed emitters
        emitters.removeAll(completedEmitters);
        System.out.println(unreadCount);
    }

    public void sendSSEEvent2(String eventName) {
        List<SSEEmitterWithStatus> completedEmitters = new ArrayList<>();
        for (SSEEmitterWithStatus emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(unreadCount));
            } catch (Exception e) {
                System.out.println("in here");
                // If there is an error sending the event, complete the emitter
                emitter.completeWithError(e);
                completedEmitters.add(emitter);
            }
        }

        // Remove completed emitters
        emitters.removeAll(completedEmitters);
        System.out.println(unreadCount);
    }

    @PostMapping(value = "/sse/close")
    public ResponseEntity<String> readAllReport () {
        unreadCount = 0;
        return ResponseEntity.ok("Read all");
    }
}