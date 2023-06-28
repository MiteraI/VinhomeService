package app.vinhomes.event.listener_storage;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.type_enum.TransactionStatus;
import app.vinhomes.event.event_storage.StartOrderCountDown;
import app.vinhomes.service.OrderService;
import app.vinhomes.service.TransactionService;
import app.vinhomes.vnpay.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class OnCreateOrder {
    private boolean flag = true;
    @Value("${time.order_timeout}")
    private int ORDER_TIMEOUT;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private VNPayService vnPayService;

    @EventListener
    public CompletableFuture<String> countingTransactionTimeout(StartOrderCountDown event) {
        try {
            System.out.println("INSIDE counting transaction, start waiting for a period of time");
            long transactionId = event.getTransactionId();
            Map<Long, String> getMap = vnPayService.getAllOrderUrlMap();
            System.out.println("All Current Url Of Order:");
            getMap.forEach((orderId,url) -> System.out.println(orderId+":  "+url));
            int MinutetoSecond = ORDER_TIMEOUT*60;
            Thread.sleep(1000 * MinutetoSecond);
            long timePeriod = event.getSecondTimeLeft();
            if (timePeriod >= ORDER_TIMEOUT) {
                Transaction getTransaction = transactionService.getTransactionById(transactionId);
                if (getTransaction != null) {
                    getMap.remove(getTransaction.getOrder().getOrderId());
                    if (getTransaction.getStatus().equals(TransactionStatus.PENDING) &&
                            getTransaction.getOrder().getStatus().equals(OrderStatus.PENDING)) {
                        System.out.println("order timeout");
                        Order getOrder = getTransaction.getOrder();
                        getTransaction.setStatus(TransactionStatus.FAIL);
                        getOrder.setStatus(OrderStatus.CANCEL);
                        boolean orderStat = orderService.updateOrderByObject(getOrder);
                        boolean transactionStat = transactionService.updateTransactionByObject(getTransaction);
                        System.out.println("INSIDE counting transaction, end thread ");
                        System.out.println("TIMEOUT DELETE SUCCESS AFTER "+timePeriod+" SECONDs");
                        return CompletableFuture.completedFuture("CompletableFuture return when finish");
                    }
                    System.out.println("async transaction has already been cancelled");
                    return CompletableFuture.completedFuture("async transaction has already been cancelled");
                } else {
                    System.out.println("async transaction null");
                    return CompletableFuture.completedFuture("CompletableFuture return when finish");
                }
            }
            System.out.println("order not yet timeout");
            return CompletableFuture.completedFuture("CompletableFuture return when finish");
        } catch (InterruptedException e) {
            System.out.println("Inside thread: " + e.getMessage());
            return CompletableFuture.completedFuture("CompletableFuture return when Exception");
        }
    }
}
