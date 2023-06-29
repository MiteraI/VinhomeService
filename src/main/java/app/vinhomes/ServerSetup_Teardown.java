package app.vinhomes;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.type_enum.TransactionStatus;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.service.OrderService;
import app.vinhomes.service.TransactionService;
import app.vinhomes.vnpay.service.VNPayService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServerSetup_Teardown {
    @Autowired
    private CustomerSessionListener listener;
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private OrderRepository orderRepository;
    @PreDestroy//before all resource is flush away , can be used to clear session data or do other stuff
    public void destroyServer()throws Exception{
        Map<Long,String> getMap = vnPayService.getAllOrderUrlMap();
        getMap.forEach((Id,url)-> {
            System.out.println("map before clean up:"+Id + "  :  "+ url);
            getMap.remove(Id);
            Transaction getTransaction = transactionRepository.findById(Id).get();System.out.println(getTransaction);
            getTransaction.getOrder().setStatus(OrderStatus.CANCEL);
            getTransaction.setStatus(TransactionStatus.FAIL);
            transactionRepository.save(getTransaction);System.out.println("save transaction");
        });
        getMap.forEach((Id,url)->System.out.println("map after clean up:"+Id + "  :  "+ url));
        listener.destroyAllSession();
        System.out.println("end server");
    }
    @PostConstruct// after all bean and service have been initialized, can be used to set basic config if you want
    public void initServer(){
        System.out.println("start server");
    }
}
