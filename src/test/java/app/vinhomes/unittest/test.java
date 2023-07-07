package app.vinhomes.unittest;


import app.vinhomes.common.ErrorChecker;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.event.event_storage.StartOrderCountDown;
import app.vinhomes.event.listener_storage.OnCreateOrder;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.security.email.email_service.EmailService;
import app.vinhomes.service.AccountService;
import app.vinhomes.service.TransactionService;
import app.vinhomes.vnpay.service.VNPayService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
//@DataJpaTest
@ExtendWith(MockitoExtension.class)// this is to replace the mock config, not sure
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class test {
@Autowired
    private ErrorChecker errorChecker;
    @Autowired
    private AccountService accountService ;//= new AccountService();

    @Value("${mail.mailType.verification}")
    private String VERIFICATION_MAIL;
    @Value("${mail.mailType.forgetAccount}")
    private String FORGETACCOUNT_MAIL;
    @Value("${mail.mailType.adminRefundTransaction}")
    private String ADMIN_REFUNDTRANSACTION_MAIL;
    @Value("${mail.mailType.orderFinish}")
    private String ORDERFINISH_MAIL;
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
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionService transactionService;

    private int expired = 120000;

    @Test
    @Disabled
    void printExpired() {
        assertThat(expired).isEqualTo(120000);
    }

    @Test
    void getTransactionVnp(){
        Transaction getTransaction = transactionRepository.findByVnpTxnRef("21307218");
        System.out.println(getTransaction);
    }
    @Test
    @Disabled
    void testTransaction() {
        VNPayService.BuildTransactionThroughOrder(
                37,
                "123123",
                "8"
        );
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
    @Disabled
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
    @Disabled
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
    @Disabled
    void testOTP(){
        System.out.println(OTP_TIMEOUT_SECOND);
    }
    @Test

    void testSendmailTemplate(){
        Account account =  accountRepository.findById(27l).get();
        //VERIFICATION_MAIL
        Transaction getTransaction = transactionRepository.findById(60l).get();
        emailService.sendMailWithTemplate(account,ADMIN_REFUNDTRANSACTION_MAIL,getTransaction);
    }
    @Test
    void getWorkerList(){
        Transaction getTransaction = transactionRepository.findById(60l).get();
        Order getOrder = getTransaction.getOrder();
        List<Account> getWorkers = getOrder.getSchedule().getWorkers();
        getWorkers.forEach(worker -> {
            if(worker.getPhones()!=null && worker.getPhones().isEmpty() == false){
                System.out.println(worker.getPhones().get(0));
            }else{
                System.out.println("this account does not have phone number");
            }
        });
    }
    @Test
    @Disabled
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
    @Test
    //@Disabled
    void testChangePassword(){
        accountService.changePassword(27l,"123new","123");
    }
    @Test
    void getImage() throws IOException {
        ClassPathResource getImage = new ClassPathResource("src/assets/images/service-1.jpg");
        //ByteArrayInputStream inputStream = new ByteArrayInputStream(getImage.getContentAsByteArray());
        ByteArrayResource getByte = new ByteArrayResource(getImage.getContentAsByteArray());
        System.out.println("success: "+getImage.getPath()+"  filename: "+getImage.getFilename());
        FileSystemResource resource = new FileSystemResource(new File("src/assets/images/service-1.jpg"));
        System.out.println(resource.getFilename());
        Account account =  accountRepository.findById(27l).get();
        emailService.sendMailWithTemplate(account,FORGETACCOUNT_MAIL);
        //InputStreamSource inputStreamSource = getImage.getInputStream();//new ByteArrayResource();
    }
    @Test
    void getUsernameThroughTransactoin(){
        Transaction getTransaction = transactionService.getTransactionByVnpTxnRef(null);
        Account getAccount = getTransaction.getOrder().getAccount();
        String getUsername = getAccount.getAccountName();
        System.out.println(getUsername);
    }
    @Test
    void testParameterHourPolicy(){

    }

    @Test
    void getInformationFromTransactionQuery(){
        String getUndoneTransaction = "{\"vnp_ResponseId\":\"c57271fd20da4fb587120c43623412fb\",\"vnp_Command\":\"querydr\",\"vnp_ResponseCode\":\"91\",\"vnp_Message\":\"Transaction_not_found\",\"vnp_TmnCode\":\"BIDAHJ80\",\"vnp_TxnRef\":\"92543108\",\"vnp_SecureHash\":\"332d4ead619642e39fcfb57570cd0e1d893201998d678d265e83b10bed165af8172543f3691258cbe9abd0b774a2ee2eeda8225c217dff42943b1dbe8e85198f\"}";
        String getFailTransaction ="{\"vnp_ResponseId\":\"e15baf36d3c84b498dea171750e2c17b\",\"vnp_Command\":\"querydr\",\"vnp_ResponseCode\":\"00\",\"vnp_Message\":\"QueryDR Success\",\"vnp_TmnCode\":\"BIDAHJ80\",\"vnp_TxnRef\":\"21307218\",\"vnp_Amount\":\"5000000\",\"vnp_OrderInfo\":\"Thanh toan don hang:21307218\",\"vnp_BankCode\":\"VNBANK\",\"vnp_PayDate\":\"20230619165517\",\"vnp_TransactionNo\":\"2275891\",\"vnp_TransactionType\":\"01\",\"vnp_TransactionStatus\":\"01\",\"vnp_Trace\":\"0\",\"vnp_FeeAmount\":\"0\",\"vnp_SecureHash\":\"3aee13cae75d1d69f2605ef0dcf44b3d741b89efd02fae5dc65f85d686a4a85f73fa68120226d37295f47b675cd3e114e0b95909923994c8ee7cc3872003ddb7\"}";
        String getSuccessTransaction = "{\"vnp_ResponseId\":\"c63baeb800374ba68d0ad6b0cb691b0d\",\"vnp_Command\":\"querydr\",\"vnp_ResponseCode\":\"00\",\"vnp_Message\":\"QueryDR Success\",\"vnp_TmnCode\":\"BIDAHJ80\",\"vnp_TxnRef\":\"91843726\",\"vnp_Amount\":\"10000000\",\"vnp_OrderInfo\":\"Thanh toan don hang:91843726\",\"vnp_BankCode\":\"NCB\",\"vnp_PayDate\":\"20230624143806\",\"vnp_TransactionNo\":\"14048284\",\"vnp_TransactionType\":\"01\",\"vnp_TransactionStatus\":\"00\",\"vnp_CardNumber\":\"970419xxxxxxxxx2198\",\"vnp_Trace\":\"2285990\",\"vnp_CardHolder\":\"NGUYEN VAN A\",\"vnp_Issuer\":\"NCB\",\"vnp_FeeAmount\":\"0\",\"vnp_SecureHash\":\"a5a3e0b4fec6e79348ffb2508e93d856b177e95d2a97597e2001e585694b0572726121f8452278783eb2656084292bb5f44399a9679b55881d59a961953b0abf\"}";
        String[] splitTransaction = getSuccessTransaction.split(",");
        for (String item : splitTransaction){
            //System.out.println(item);
        }
        SimpleDateFormat getFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        //System.out.println(splitTransaction[2]);
        System.out.println(splitTransaction[2].split(":")[1].substring(1,3));
        //System.out.println(splitTransaction[3]);System.out.println(splitTransaction[3].split(":")[1].substring(1));
//        System.out.println(splitTransaction[5]);System.out.println(splitTransaction[5].split(":")[1].substring(1));
        String amount = splitTransaction[6].split(":")[1];
        System.out.println(amount.substring(1,amount.length()-1));
//        System.out.println(splitTransaction[7]);System.out.println(splitTransaction[7].split(":")[1].substring(1));
//        System.out.println(splitTransaction[8]);System.out.println(splitTransaction[8].split(":")[1].substring(1));
        String paydate = splitTransaction[9].split(":")[1];
        System.out.println(paydate.substring(1,paydate.length()-1));
//        System.out.println(splitTransaction[10]);System.out.println(splitTransaction[10].split(":")[1].substring(1));


        //System.out.println(splitTransaction[12]);
        System.out.println(splitTransaction[12].split(":")[1].substring(1,3));

        //        System.out.println(splitTransaction[3]);
//        System.out.println(splitTransaction[5]);
//        System.out.println(splitTransaction[6]);
//        System.out.println(splitTransaction[7]);
//        System.out.println(splitTransaction[8]);
//        System.out.println(splitTransaction[9]);
//        System.out.println(splitTransaction[10]);
//        System.out.println(splitTransaction[12]);
//        String getDate = splitTransaction[9].split(":")[1];
//        String removeBracket=  getDate.substring(1,getDate.length()-1);
//        System.out.println(getDate);
//        System.out.println(removeBracket);
    }

}
