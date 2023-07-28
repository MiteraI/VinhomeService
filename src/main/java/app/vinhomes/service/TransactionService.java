package app.vinhomes.service;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.type_enum.TransactionStatus;
import app.vinhomes.event.event_storage.SendEmailOnRefund_OnFinishOrder;
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
    @Autowired
    private OrderService orderService;

    public List<Map<String, String>> getAllTransactionAndFormat() {
        List<Transaction> getAllTransaction = transactionRepository.findAll();
        List<Map<String, String>> returnAsListJsonFormat = new LinkedList<>();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        getAllTransaction.stream().forEach(transaction -> {
            Map<String, String> JsonFormat = new HashMap<>();
            JsonFormat.put("transactionID", transaction.getTransactionId().toString().trim());
            JsonFormat.put("bankCode", transaction.getBankCode().trim());
            JsonFormat.put("paymentMethod", transaction.getPaymentMethod().trim());
            JsonFormat.put("status", transaction.getStatus().name().trim());
            JsonFormat.put("transactionDate", formatDate.format(new Date(transaction.getVnpTransactionDate())).toString().trim());
            JsonFormat.put("vnp_txnRef", transaction.getVnpTxnRef().trim());
            returnAsListJsonFormat.add(JsonFormat);
        });
        return returnAsListJsonFormat;
    }

    public String getSingleTransactionInfo(String transactionId) {
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
            return "[" + returnTransaction + "," + returnOrder + "," + returnAccount + "]";
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ResponseEntity<String> queryVNpayWithOrderID(String orderId, HttpServletRequest request, HttpServletResponse response) {
        try {
            long parseOrderId = Long.parseLong(orderId);
            Order order = orderRepository.findById(parseOrderId).get();
            Transaction getTransaction = transactionRepository.findById(order.getOrderId()).get();
            String getVnp_txnRef = getTransaction.getVnpTxnRef();
            long transactionDate = getTransaction.getVnpTransactionDate();
            ResponseEntity callingResult = vnPayService.queryVNPAY(getVnp_txnRef, transactionDate, request, response);
            if (callingResult.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body(callingResult.getBody().toString());
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("VNPAY SERVICE ERROR");
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR PARSEING ORDER ID");
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR NULL POINTER");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR SERVER");
        }
    }

    public ResponseEntity<String> queryVNpayWithVnp_txtRef(String vnp_txnRef, HttpServletRequest request, HttpServletResponse response) {
        try {
            //TODO: chinh lai findByVnpTxnRef()
            //Transaction getTransaction = transactionRepository.findByVnpTxnRef(vnp_txnRef);
            Transaction getTransaction = transactionRepository.findByVnpTxnRef(vnp_txnRef);
            String getVnp_txnRef = getTransaction.getVnpTxnRef();
            long transactionDate = getTransaction.getVnpTransactionDate();
            ResponseEntity callingResult = vnPayService.queryVNPAY(getVnp_txnRef, transactionDate, request, response);
            if (callingResult.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok().body(callingResult.getBody().toString());
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("VNPAY SERVICE ERROR");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR SERVER");
        }
    }

    public ResponseEntity<String> refundWithOrderID(String orderId, HttpServletRequest request, HttpServletResponse response) {
        try {
            long parsedOrderId = Long.parseLong(orderId);
            Order getOrder = orderRepository.findById(parsedOrderId).get();
            Transaction getTransaction = transactionRepository.findById(getOrder.getOrderId()).get();
            String vnp_txtRef = getTransaction.getVnpTxnRef();
            String getAccountName = getOrder.getAccount().getAccountName();
            String transactionDate = String.valueOf(getTransaction.getVnpTransactionDate());
            int amount = (int) getOrder.getPrice();
            if (getTransaction.getPaymentMethod().equals("COD")) {
                System.out.println("Yes cancel COD order");
                orderService.setOrderStatus(getOrder,OrderStatus.CANCEL);
                this.setTransactionStatus(getTransaction,TransactionStatus.FAIL);
                return ResponseEntity.ok().body("YES, COD ORDER CANCELLED");
            } else {
                Map<Long, String> getMapUrl = vnPayService.getAllOrderUrlMap();
                String url = getMapUrl.get(getOrder.getOrderId());
                Long getOrder_Id = getOrder.getOrderId();
                String getExpiredTransactionDate = vnPayService.getCreateTimeFromUrlMap(getOrder_Id);
                boolean checkIf_TransactionExpired = true;
                if(getExpiredTransactionDate != null){
                    checkIf_TransactionExpired = vnPayService.checkTransactionExpired(getExpiredTransactionDate);
                }else{
                    checkIf_TransactionExpired= true;
                }
                if (checkIf_TransactionExpired == false) {
                    //TODO : tim cach su exception cancel order
                    System.out.println("yes cancel order, when order is not yet timeout");
                    orderService.setOrderStatus(getOrder,OrderStatus.CANCEL);
                    this.setTransactionStatus(getTransaction,TransactionStatus.FAIL);
                    vnPayService.deleteOrderUrlMapItem(getOrder_Id);
                    return ResponseEntity.ok().body("YES, CANCEL SUCCESS");
                } else {
                    //////////////
                    ResponseEntity responseQuery = queryVNpayWithVnp_txtRef(vnp_txtRef,request,response);
                    if(responseQuery.getStatusCode().is2xxSuccessful()) {
                        String getQuery = (String) responseQuery.getBody();
                        String getResponseCode = vnPayService.extractResponseCode(getQuery);
                        if (getResponseCode.equals("00")) {
                            String getTransactionStatus = vnPayService.extractTransactionStatusQuery(getQuery);
//                            String getAmount = vnPayService.extractTransactionAmount(getQuery);
//                            String getPaydate = vnPayService.extractTransactionPayDateQuery(getQuery);
                            if(getTransactionStatus.equals("00")){
                                ResponseEntity callingResult = vnPayService.refund(vnp_txtRef, transactionDate, amount, getAccountName, request, response);
                                if (callingResult.getStatusCode().is2xxSuccessful()) {
                                    //tach tung phan trong body ra thanh tung cuc de lay response code
                                    String[] getBodyToArray = callingResult.getBody().toString().split(",");
                                    String getResponseCodeRefund
                                            = Arrays.stream(getBodyToArray).toList().get(2).split(":")[1];/// lay response code trong doan string
                                    if (getResponseCodeRefund.equals("\"00\"")) {
                                        System.out.println("yes refund sent and success, wait for the bank to response");
                                        orderService.setOrderStatus(getOrder,OrderStatus.CANCEL);
                                        this.setTransactionStatus(getTransaction,TransactionStatus.REFUNDED);
                                        vnPayService.deleteOrderUrlMapItem(getOrder_Id);
                                        return ResponseEntity.ok().body("refund Success");
                                    } else {
                                        System.out.println("fail to refund, try again");
                                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("FAIL TO REFUND, TRY AGAIN");
                                    }
                                }
                                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("VNPAY SERVICE ERROR, TRY AGAIN LATER");
                            }
                            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("This transaction is not appropriate for refund, might be you haven't paid for it yet");
                        }
                    }else{
                        System.out.println("ERROR query: "+ responseQuery.getBody());
                        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("ERROR EXCEPTION IN API");
                    }
                    //////////////
                }
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("ERROR EXCEPTION IN API, second row");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR SERVER");
        }
    }

    public String getVnp_txtRefThroughOrderId(String orderId) {
        try {
            long parsedOrderId = Long.parseLong(orderId);
//            Order getOrder = orderRepository.findById(parsedOrderId).get();
            // order id is the same as transaction id, both are mapped together
            Transaction getTransaction = transactionRepository.findById(parsedOrderId).get();
            return getTransaction.getVnpTxnRef();
        } catch (NumberFormatException e) {
            System.out.println("inside transactionService:  " + e.getMessage());
            return null;
        } catch (NoSuchElementException e) {
            System.out.println("inside transactionService:  " + e.getMessage());
            return null;
        }
    }

    public Transaction getTransactionById(long id) {
        try {
            return transactionRepository.findById(id).get();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean updateTransactionByObject(Transaction transaction) {
        try {
            transactionRepository.save(transaction);
            return true;
        } catch (Exception e) {
            System.out.println("update error: " + e.getMessage());
            return false;
        }
    }

    public boolean checkIfTransactionIsPending_IsExist(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId).get();
            Transaction getTransaction = transactionRepository.findById(order.getOrderId()).get();
            if (getTransaction.getStatus().equals(TransactionStatus.PENDING)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Transaction getTransactionByVnpTxnRef(String vnpTxnref){
        try{
            return transactionRepository.findByVnpTxnRef(vnpTxnref);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void setTransactionStatus(Transaction transaction, TransactionStatus status){
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }
}
