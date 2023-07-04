package app.vinhomes.unittest;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Transaction;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.security.email.email_service.EmailService;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Future;
@Service
public class asyncClass {
    private int time1 = 0;
    public boolean flag = false;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Value("${mail.mailType.adminRefundTransaction}")
    private String ADMIN_REFUNDTRANSACTION_MAIL;
    @Async("asyncExecutor")
    public CompletableFuture<String> doSomeStuff() {
        LocalDateTime start = LocalDateTime.now();
        System.out.println("inside doSomeStuff - thread: "+ Thread.currentThread().getName());
        try {
            time1 += 1;
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("flag in 1: "+ flag);
        if (flag) {
            System.out.println("Flag fail");
            System.out.println(Duration.between(LocalDateTime.now(), start).abs().getSeconds());
            return CompletableFuture.completedFuture("Flag fail to happen");
        }
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //System.out.println("yes flag works");
        System.out.println(" end doSomeStuff1 " + time1);
        System.out.println(Duration.between(LocalDateTime.now(), start).abs().getSeconds());
        //Thread.currentThread().stop();
        return CompletableFuture.completedFuture("done so something");
    }

    @Async("asyncExecutor")
    public CompletableFuture<LocalDateTime> doSomeStuff2() {

        LocalDateTime start = LocalDateTime.now();
        System.out.println("inside doSomeStuff2 - thread: "+ Thread.currentThread().getName());
        try {
            Thread.sleep(3000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }catch (CancellationException e){
            System.out.println("cancel doSomeStuff2");
            return CompletableFuture.completedFuture(LocalDateTime.now());
        }
        System.out.println(" end doSomeStuff2");
        System.out.println(Duration.between(LocalDateTime.now(), start).abs().getSeconds());
        return CompletableFuture.completedFuture(LocalDateTime.now());
    }
    @Async("asyncExecutor")
    public Future<String> doSomeStuff3() {
        try {
            LocalDateTime start = LocalDateTime.now();
            System.out.println("inside doSomeStuff3 - thread: "+ Thread.currentThread().getName());
            Thread.currentThread().wait(3000l);
            System.out.println(" end doSomeStuff3");
            System.out.println(Duration.between(LocalDateTime.now(), start).abs().getSeconds());
            return CompletableFuture.completedFuture("doneStuff3");
        }catch (CompletionException e){
            System.out.println("doSomeStuff3 is cancelled");
            return CompletableFuture.completedFuture("cancel doneStuff3");
        } catch (InterruptedException e) {
            //Thread.currentThread().interrupt();
            System.out.println("doSomeStuff3 is cancelled");
            return CompletableFuture.completedFuture("cancled");
        }
    }
    @Async("asyncExecutor")
    public CompletableFuture<String> sendEmail(){
        Account account =  accountRepository.findById(1l).get();
        //VERIFICATION_MAIL
        Transaction getTransaction = transactionRepository.findById(60l).get();
        return CompletableFuture.completedFuture( emailService.sendMailWithTemplate(account,ADMIN_REFUNDTRANSACTION_MAIL,getTransaction));
    }
    public void setFlag(boolean flag){
        this.flag = flag;
    }

}
