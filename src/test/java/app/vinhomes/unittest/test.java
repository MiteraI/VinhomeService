package app.vinhomes.unittest;


import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.event.event_storage.StartOrderCountDown;
import app.vinhomes.event.listener_storage.OnCreateOrder;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.security.email.email_service.EmailService;
import app.vinhomes.vnpay.service.VNPayService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.ParameterTypes;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
//@DataJpaTest
//@ExtendWith(MockitoExtension.class)// this is to replace the mock config, not sure
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class test {
    @Value("${time.hourpolicy}")
    private int plusHour;
    @Value("${time.order_timeout}")
    private long ORDER_TIMEOUT;
    @Value("${time.otp_timeout}")
    private int OTP_TIMEOUT_SECOND;
    @Autowired
    private VNPayService VNPayService;
    @Autowired
    private AccountRepository  accountRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VNPayService vnPayService;
    private int expired = 120000;

    @Test
    @Disabled
    void printExpired() {
        assertThat(expired).isEqualTo(120000);
    }

    @Test
    @Disabled
    void testTransactionRepo() {

        //List<Transaction> transactionList= transactionRepository.findAll();
        //int size = transactionList.size();
    }

    @Test
    void testTransaction() {
        VNPayService.BuildTransactionThroughOrder(

                37,
                "123123",
                "8"
        );
    }

    @Test
    void testTransactionRepoFunction() {
        //Transaction transaction = VNPayService.getTransaction("23226995");
        //System.out.println(transaction);
    }

    @Test
    void testGetParamName() {
        try {
            Field[] list = Order.class.getDeclaredFields();
            Object order = new Order(
                    1l, 100000, OrderStatus.CANCEL,
                    null,
                    null,
                    null,
                    null,
                    5,
                    ""
                    , null
            );
            List<Field> list2 = Arrays.stream(list).toList();
            System.out.println(list2.isEmpty());
            for (Field item : list2) {
                System.out.print(item.getName());
                System.out.print("    " + item.getType().getTypeName());
                System.out.println("    " + item.get(order));
            }
            // chi lay dc neu cac tham so la public,
        } catch (Exception e) {
            System.out.println(e.getMessage() + "   " + e.getCause());
        }

    }

    @Test
    @Disabled
    void printList() {
        String getString = "{\"vnp_ResponseId\":\"290bc1fea6f14f0fb620c8ed3cff5ae5\",\"vnp_Command\":\"refund\",\"vnp_ResponseCode\":\"00\",\"vnp_Message\":\"SUCCESS\",\"vnp_TmnCode\":\"BIDAHJ80\",\"vnp_TxnRef\":\"93698609\",\"vnp_Amount\":\"15000000\",\"vnp_OrderInfo\":\"Hoan tien GD OrderId:93698609\",\"vnp_BankCode\":\"NCB\",\"vnp_PayDate\":\"20230617095912\",\"vnp_TransactionNo\":\"14041161\",\"vnp_TransactionType\":\"02\",\"vnp_TransactionStatus\":\"05\",\"vnp_SecureHash\":\"e9d99c911176bacf143244fb8122ed808e2e302b88dad0c5171aa4fd843cc83da6ca2181ad0680c641d48d23d8a50818d6299fc58bc07037621c513a1f3613b9\"}\n";
        String[] getStingArray = getString.split(",");
        Arrays.stream(getStingArray).toList().forEach(item -> System.out.println(item));
        System.out.println(Arrays.stream(getStingArray).toList().get(2).split(":")[1]);
    }

    @Test
    void plusTime() {
        LocalDateTime time = LocalDateTime.now();
        LocalDateTime timeAfter2 = time.plusHours(2);
        System.out.println("plus hour: " + plusHour);
        System.out.println(time);
        System.out.println(timeAfter2);
        System.out.println(timeAfter2.isBefore(time));
        System.out.println(timeAfter2.isAfter(time));
    }

    @Test
    void testDuration() {
        System.out.println(ORDER_TIMEOUT);
        LocalDateTime from = LocalDateTime.now();
        LocalDateTime to = LocalDateTime.now().minusDays(2);
        Duration diff = Duration.between(from, to);
        System.out.println(diff.toString());

        System.out.println(diff.getSeconds());
    }
    @Test
    @Disabled
    void testAsyncOrderTimeout() throws InterruptedException {
        applicationEventPublisher.publishEvent(new StartOrderCountDown(LocalDateTime.now(),8l));
        Thread.sleep(10000l);
        System.out.println("main thread open");
        Thread.sleep(20000l);
    }
    @Test
    void testOTP(){
        System.out.println(OTP_TIMEOUT_SECOND);
    }
    @Test
    void testSendmailTemplate(){
        Account account =  accountRepository.findById(27l).get();
        emailService.sendMailWithTemplate(account);
    }
    @Test
    void testGetUrlTime(){
        Map<Long,String>  getMap  = new HashMap<>();
        getMap.put(1l,"https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=10000000&vnp_BankCode=VNBANK&vnp_Command=pay&vnp_CreateDate=20230627222249&vnp_CurrCode=VND&vnp_ExpireDate=20230627222449&vnp_IpAddr=0%3A0%3A0%3A0%3A0%3A0%3A0%3A1&vnp_Locale=vn&vnp_OrderInfo=Thanh+toan+don+hang%3A24943851&vnp_OrderType=billpayment&vnp_ReturnUrl=http%3A%2F%2Flocalhost%3A8080%2Fvnpay%2Freturnurl&vnp_TmnCode=BIDAHJ80&vnp_TxnRef=24943851&vnp_Version=2.1.0&vnp_SecureHash=675f34197851a4c04f02ca352ba2b9f473178b8075227c74052262511c8faa251634e3b4dd186fd4852c9bcf391f38130775dd1617d9b7a1940c9533dca1d68d");
        String[] getSplitString = getMap.get(1l).split("&");
        for(String item : getSplitString){
        }
        System.out.println(getSplitString[5]);
        String getExpiredDate = getSplitString[5].split("=")[1];
        //System.out.println(getExpiredDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try{
            long parsedDate  = Long.parseLong(getExpiredDate);
            System.out.println(parsedDate);
            Date fixDateToString = format.parse(getExpiredDate);
            Date timeNow = new Date(System.currentTimeMillis());
            System.out.println(fixDateToString);
            System.out.println(timeNow);
            boolean checkIfExpired = fixDateToString.before(timeNow);
            System.out.println(checkIfExpired);
            if(checkIfExpired){
                System.out.println("order has been expired");
            }
        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
