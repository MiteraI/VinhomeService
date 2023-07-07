package app.vinhomes.event;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class SSEEmitterWithStatus extends SseEmitter {
    private boolean read;

    public SSEEmitterWithStatus() {
        super();
        this.read = false;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}