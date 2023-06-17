package app.vinhomes.unittest;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.vnpay.service.VNPayService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.ParameterTypes;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.*;
@SpringBootTest
//@DataJpaTest
//@ExtendWith(MockitoExtension.class)// this is to replace the mock config, not sure
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class test {
    @Value("${time.hourpolicy}")
    private int plusHour;
    @Autowired
    private VNPayService VNPayService;
//    @Value("${twilio.expiration}")
    private int expired = 120000;

    @Test
    @Disabled
    void printExpired(){
        assertThat(expired).isEqualTo(120000);
    }
    @Test
    @Disabled
    void testTransactionRepo(){
        //List<Transaction> transactionList= transactionRepository.findAll();
        //int size = transactionList.size();
    }
    @Test
    void testTransaction(){
        VNPayService.BuildTransactionThroughOrder(
            37,
                "123123",
                "8"
        );
    }
    @Test
    void testTransactionRepoFunction(){
        //Transaction transaction = VNPayService.getTransaction("23226995");
        //System.out.println(transaction);
    }
    @Test
    void testGetParamName()  {
        try {
            Field[] list = Order.class.getDeclaredFields();
            Object order = new Order(
                    1l,100000,OrderStatus.CANCEL,
                    null,
                    null,
                    null,
                    null,
                    5,
                    ""
                    ,null
            );
            List<Field> list2 = Arrays.stream(list).toList();
            System.out.println(list2.isEmpty());
            for(Field item: list2){
                System.out.print(item.getName());
                System.out.print("    "+ item.getType().getTypeName());
                System.out.println("    "+ item.get(order));
            }
            // chi lay dc neu cac tham so la public,
        }catch (Exception e ){
            System.out.println(e.getMessage()+ "   "+ e.getCause());
        }

    }
    @Test
    @Disabled
    void printList(){
        String getString = "{\"vnp_ResponseId\":\"290bc1fea6f14f0fb620c8ed3cff5ae5\",\"vnp_Command\":\"refund\",\"vnp_ResponseCode\":\"00\",\"vnp_Message\":\"SUCCESS\",\"vnp_TmnCode\":\"BIDAHJ80\",\"vnp_TxnRef\":\"93698609\",\"vnp_Amount\":\"15000000\",\"vnp_OrderInfo\":\"Hoan tien GD OrderId:93698609\",\"vnp_BankCode\":\"NCB\",\"vnp_PayDate\":\"20230617095912\",\"vnp_TransactionNo\":\"14041161\",\"vnp_TransactionType\":\"02\",\"vnp_TransactionStatus\":\"05\",\"vnp_SecureHash\":\"e9d99c911176bacf143244fb8122ed808e2e302b88dad0c5171aa4fd843cc83da6ca2181ad0680c641d48d23d8a50818d6299fc58bc07037621c513a1f3613b9\"}\n";
        String[] getStingArray = getString.split(",");
        Arrays.stream(getStingArray).toList().forEach(item -> System.out.println(item));
        System.out.println(Arrays.stream(getStingArray).toList().get(2).split(":")[1]);
    }

    @Test
    void plusTime(){
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime timeAfter2 = time.plusHours(2);
        System.out.println("plus hour: "+ plusHour);
        System.out.println(time);
        System.out.println(timeAfter2);
        System.out.println(timeAfter2.isBefore(time));
        System.out.println(timeAfter2.isAfter(time));
    }
}
