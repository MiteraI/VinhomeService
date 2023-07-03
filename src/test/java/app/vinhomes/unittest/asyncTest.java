package app.vinhomes.unittest;

import app.vinhomes.AsyncConfig;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.util.concurrent.*;

@SpringBootTest
public class asyncTest {
    @Autowired
    private asyncClass asyncMethod;
    @Autowired
    private AsyncConfig asyncConfig;
    @Value("${mail.mailType.adminRefundTransaction}")
    private String ADMIN_REFUNDTRANSACTION_MAIL;

    //@Test
    @Disabled
    void testAsyncResult() throws ExecutionException, InterruptedException {
        System.out.println("main thread start");
        try {
            CompletableFuture<String> result = asyncMethod.doSomeStuff();
            CompletableFuture<LocalDateTime> result2 = asyncMethod.doSomeStuff2();
            if (result.isDone() == false) {
                Thread.sleep(500l);
                System.out.println("result1 not done yet");
            }
            if (result2.isDone() == false) {
                Thread.sleep(500l);
                System.out.println("result2 not done yet");
            }

            //asyncMethod.setFlag(false);
            while(true){
                if(result.isDone() == false && result2.isDone() == false){
                    System.out.println("1 and 2 is not done");
                }
                if(!result.isDone()){
                    try {
                        result.get(100, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException e) {
                        System.out.println("exception result1 is thrown");
                        asyncMethod.setFlag(true);
                        result.cancel(true);
                    }



                }
                if(!result2.isDone()){
                    result2.cancel(true);
                }
                if(result.isDone()&& result2.isDone()){
                    System.out.println("both is  done");
                    break;
                }
                Thread.sleep(500);
            }
            //result3.cancel(true);

            //String  result1get = asyncMethod.doSomeStuff().getNow("result 1 not done yet so this throw");
            System.out.println("main thread sleep ");
            Thread.sleep(7000l);
            try {
                System.out.println(result.get() +"   "+ result2.get().toString());
            } catch (Exception e) {
                System.out.println("result 1 has been cancel so it throws, gud ");

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("main thread finished");
    }

    @Test
    void testAsyncMail() throws ExecutionException, InterruptedException {
        System.out.println("start the send email in async ");
        CompletableFuture<String> result = asyncMethod.sendEmail();
        System.out.println("now wait for the result, this should be paused while result is not there or any exception is throw");
        System.out.println("yes wait for result success while mail is sent "+ result.get());
    }
}
