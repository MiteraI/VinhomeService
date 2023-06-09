package app.vinhomes.unittest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.AssertionsForClassTypes.*;

//@SpringBootTest
public class test {
//    @Value("${twilio.expiration}")
    private int expired = 120000;

    @Test
    void printExpired(){
        assertThat(expired).isEqualTo(120000);
    }
}
