package app.vinhomes.service;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.type_enum.TransactionStatus;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.vnpay.service.VNPayService;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TransactionService {
    @Value("${time.hourpolicy}")
    private int HourPolicy;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private VNPayService vnPayService;
    public List<Map<String,String>> getAllTransactionAndFormat(){
        List<Transaction> getAllTransaction = transactionRepository.findAll();
        List<Map<String,String>> returnAsListJsonFormat = new LinkedList<>();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        getAllTransaction.stream().forEach(transaction ->{
            Map<String,String> JsonFormat = new HashMap<>();
            JsonFormat.put("transactionID",transaction.getTransactionId().toString().trim());
            JsonFormat.put("bankCode",transaction.getBankCode().trim());
            JsonFormat.put("paymentMethod",transaction.getPaymentMethod().trim());
            JsonFormat.put("status",transaction.getStatus().name().trim()   );
            JsonFormat.put("transactionDate",formatDate.format(new Date(transaction.getVnpTransactionDate())).toString().trim());
            JsonFormat.put("vnp_txnRef",transaction.getVnpTxnRef().trim());
            returnAsListJsonFormat.add(JsonFormat);
        });
        return returnAsListJsonFormat;
    }
    public String getSingleTransactionInfo(String transactionId){
        /// need 2 param so that it can never be 2 identical vnp_txnRef
        try {
            long parsedTransactionId = Long.parseLong(transactionId);
            Transaction getTransaction = transactionRepository.findById(parsedTransactionId).get();
            Order getOrder = getTransaction.getOrder();
            Account getAccount = getOrder.getAccount();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String returnTransaction = mapper.writeValueAsString(getTransaction);
            String returnOrder = mapper.writeValueAsString(getOrder);
            String returnAccount = mapper.writeValueAsString(getAccount);
            return "["+returnTransaction+","+returnOrder+","+returnAccount+"]";
        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
            return null;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public ResponseEntity<String> queryVNpayWithOrderID(String orderId, HttpServletRequest request, HttpServletResponse response){
        try {
            long parseOrderId = Long.parseLong(orderId);
            Order order = orderRepository.findById(parseOrderId).get();
            Transaction getTransaction = transactionRepository.findById(order.getOrderId()).get();
            String getVnp_txnRef = getTransaction.getVnpTxnRef();
            long transactionDate = getTransaction.getVnpTransactionDate();
            ResponseEntity callingResult = vnPayService.queryVNPAY(getVnp_txnRef,transactionDate,request,response);
            if(callingResult.getStatusCode().is2xxSuccessful()){
                return ResponseEntity.ok().body(callingResult.getBody().toString());
            }else{
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("VNPAY SERVICE ERROR");
            }
        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR PARSEING ORDER ID");
        }catch (NullPointerException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR NULL POINTER");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR SERVER");
        }
    }
    public ResponseEntity<String> queryVNpayWithVnp_txtRef(String vnp_txnRef, HttpServletRequest request, HttpServletResponse response){
        try {
            Transaction getTransaction = transactionRepository.findByVnpTxnRef(vnp_txnRef);
            String getVnp_txnRef = getTransaction.getVnpTxnRef();
            long transactionDate = getTransaction.getVnpTransactionDate();
            ResponseEntity callingResult = vnPayService.queryVNPAY(vnp_txnRef,transactionDate,request,response);
            if(callingResult.getStatusCode().is2xxSuccessful()){
                return ResponseEntity.ok().body(callingResult.getBody().toString());
            }else{
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("VNPAY SERVICE ERROR");
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR SERVER");
        }
    }
    public ResponseEntity<String> refundVNpayWithOrderID(String orderId, HttpServletRequest request,HttpServletResponse response){
        try{
            long parsedOrderId = Long.parseLong(orderId);
            Order getOrder = orderRepository.findById(parsedOrderId).get();
            Transaction getTransaction = transactionRepository.findById(getOrder.getOrderId()).get();
            String vnp_txtRef = getTransaction.getVnpTxnRef();
            String getAccountName = getOrder.getAccount().getAccountName();
            String transactionDate =String.valueOf(getTransaction.getVnpTransactionDate());
            int amount =(int) getOrder.getPrice();
            ResponseEntity callingResult = vnPayService.refund(vnp_txtRef,transactionDate,amount,getAccountName,request,response);
            if(callingResult.getStatusCode().is2xxSuccessful()){
                //tach tung phan trong body ra thanh tung cuc de lay response code
                String[] getBodyToArray = callingResult.getBody().toString().split(",");
                String getResponseCode
                        = Arrays.stream(getBodyToArray).toList().get(2).split(":")[1];/// lay response code trong doan string
                if(getResponseCode != "00"){// chi moi  ma 00 la hop le, con lai la do loi xay ra nen se xu ly khac
                    System.out.println("yes refund sent and success, wait for the bank to response");
                    getOrder.setStatus(OrderStatus.CANCEL);
                    getTransaction.setStatus(TransactionStatus.FAIL);
                    orderRepository.save(getOrder);
                    transactionRepository.save(getTransaction);
                    return ResponseEntity.ok().body("YES, REFUND SUCCESS, WAIT FOR THE BANK");
                }
                else{
                    System.out.println("fail to refund, try again");
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("FAIL TO REFUND, TRY AGAIN");
                }
            } else{
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("VNPAY SERVICE ERROR");
            }
        }   catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR SERVER");
        }
    }
    public boolean checkIfOver_2_hourPolicy(String orderId){
        try{
            long parsedOrderId = Long.parseLong(orderId);
            Order getOrder = orderRepository.findById(parsedOrderId).get();
            // neu + 2 tieng ma van before, tuc la da qua 2 tieng => no refund (boolean = true)
            // else cho refund   (boolean = false)
            boolean isCreateTimePassLimit =  getOrder.getCreateTime().plusHours(HourPolicy).isBefore(LocalDateTime.now());
            if(isCreateTimePassLimit == false){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    public String getVnp_txtRefThroughOrderId(String orderId){
        try{
            long parsedOrderId = Long.parseLong(orderId);
//            Order getOrder = orderRepository.findById(parsedOrderId).get();
            // order id is the same as transaction id, both are mapped together
            Transaction getTransaction = transactionRepository.findById(parsedOrderId).get();
            return getTransaction.getVnpTxnRef();
        }catch (NumberFormatException e){
            System.out.println("inside transactionService:  "+e.getMessage());
            return null;
        }catch (NoSuchElementException e){
            System.out.println("inside transactionService:  "+e.getMessage());
            return null;
        }
    }
}