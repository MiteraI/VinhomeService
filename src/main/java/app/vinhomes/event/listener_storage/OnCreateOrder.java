package app.vinhomes.event.listener_storage;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.type_enum.TransactionStatus;
import app.vinhomes.event.event_storage.StartOrderCountDown;
import app.vinhomes.service.OrderService;
import app.vinhomes.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class OnCreateOrder {
    @Value("${time.order_timeout}")
    private long  ORDER_TIMEOUT;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionService transactionService;
    @EventListener
    public CompletableFuture<String> countingTransactionTimeout(StartOrderCountDown event){
        try {
            System.out.println("INSIDE counting transaction, start waiting for a period of time");
            long transactionId = event.getTransactionId();
            Thread.sleep(1000*ORDER_TIMEOUT);
            long timePeriod = event.getSecondTimeLeft();
//            while(true){
                if(timePeriod >= ORDER_TIMEOUT){
                    Transaction getTransaction = transactionService.getTransactionById(transactionId);
                    if(getTransaction != null){
                        if(getTransaction.getStatus().equals(TransactionStatus.PENDING) &&
                        getTransaction.getOrder().getStatus().equals(OrderStatus.PENDING)){
                            System.out.println("order timeout");
                            Order getOrder = getTransaction.getOrder();
                            getTransaction.setStatus(TransactionStatus.FAIL);
                            getOrder.setStatus(OrderStatus.CANCEL);
                            boolean orderStat = orderService.updateOrderByObject(getOrder);
                            boolean transactionStat = transactionService.updateTransactionByObject(getTransaction);
                            System.out.println("INSIDE counting transaction, end thread ");
                            //return true;
                            return CompletableFuture.completedFuture("CompletableFuture return when finish");
                        }
                    }else{
                        System.out.println("order not yet timeout");
                        //return false;
                        return CompletableFuture.completedFuture("CompletableFuture return when finish");
                    }
                }
                //return false;
            return CompletableFuture.completedFuture("CompletableFuture return when finish");
            //}
        } catch (InterruptedException e) {
            System.out.println("Inside thread: "+e.getMessage());
            //return false;
            return CompletableFuture.completedFuture("CompletableFuture return when Exception");
        }
    }
}
