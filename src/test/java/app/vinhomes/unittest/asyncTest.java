package app.vinhomes.unittest;

import app.vinhomes.TryEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class asyncTest {
    @Autowired
    private TryEventPublisher eventPublisher;
    @Test
    void testAsync() throws InterruptedException {
        eventPublisher.tryPublishEvent("just a normal string, nothing special");
        Thread.sleep(4000);
    }

}
