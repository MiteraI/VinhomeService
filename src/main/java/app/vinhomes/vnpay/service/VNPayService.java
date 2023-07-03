package app.vinhomes.vnpay.service;

import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.entity.order.Service;
import app.vinhomes.entity.type_enum.OrderStatus;
import app.vinhomes.entity.type_enum.TransactionStatus;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.repository.order.PaymentRepository;
import app.vinhomes.repository.order.ServiceRepository;
import app.vinhomes.service.TransactionService;
import app.vinhomes.vnpay.config.ConfigVNpay;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@org.springframework.stereotype.Service
public class VNPayService {
    private final String transactionTypeValue = "02";
    private Map<Long, String> orderUrlMap = new HashMap<>();
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private PaymentRepository paymentRepository;


    public String getBankCode_CheckCOD_VNpay(JsonNode jsonNode, HttpServletRequest request) {
        try {
            String transactionMethod = jsonNode.get("paymentId").asText();//request.getParameter("transactionMethod");//
            String getPaymentMethod = paymentRepository.findById(Long.parseLong(transactionMethod)).get().getPaymentName().trim();
            String bankCode = "";// request.getParameter("bankcode");
            if (getPaymentMethod.equals("COD")) {
                return bankCode;
            } else {
                //bankCode = jsonNode.get("bankcode").asText();
                bankCode = "VNBANK";
                System.out.println(bankCode);
                return bankCode;
            }
        } catch (NullPointerException e) {
            System.out.println("error with getting bank code");
            System.out.println(e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("error with get transaction_method");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public double getServicePriceFromOrder(String orderID) {
        long id = Long.valueOf(orderID);
        try {
            boolean checkIfExistService = orderRepository.findById(id).isPresent();
            if (checkIfExistService) {
                Service service = serviceRepository.findById(orderRepository.findById(id).get().getService().getServiceId()).get();
                System.out.println(service);
                double price = Double.parseDouble(String.valueOf(service.getPrice()));
                return price;
            } else {
                return -1;
            }
        } catch (Exception e) {
            System.out.println("error getting service from db");
            System.out.println(e.getMessage());
            return -1;
        }
    }

    public Transaction BuildTransactionThroughOrder(long orderId, String vnp_txnRef, String paymentMethodID) {
        try {
            Order getOrder = orderRepository.findById(orderId).get();
            if (getOrder != null) {
                Transaction toSaveTransaction =
                        Transaction.builder()
                                .vnpTxnRef(vnp_txnRef)
                                .paymentMethod(paymentRepository.findById(Long.valueOf(paymentMethodID)).get().getPaymentName().toString())
                                .transactionId(getOrder.getOrderId())
                                .order(getOrder)
                                .status(TransactionStatus.PENDING)
                                .build();
                System.out.println(toSaveTransaction);

                System.out.println("done build transaction, now safe");
                return transactionRepository.save(toSaveTransaction);
            }
        } catch (NullPointerException e) {
            System.out.println("WHY cant find Order with its own ID, major error    " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("something happen when savein transaction, might be due to not having enough fields");
            return null;
        }
        System.out.println("some error happen inside BuildTransactionThroughOrder");
        return null;
    }

    public boolean getOrderStatusThrough_vnp_txnRef(String vnp_txnRef) {
        System.out.println("inside checking order status after vnpay return response");
        Transaction getTransaction;//= transactionRepository;
        return false;
    }

    public Order getOrderByVnp_txnRef(String vnp_txnref) {
        try {
            //long getOrderID = transactionRepository.findByVnpTxnRef(vnp_txnref).getOrder().getOrderId();
            long getOrderID = transactionRepository.findByVnpTxnRef(vnp_txnref).getOrder().getOrderId();
            Order getOrder = orderRepository.findById(getOrderID).get();
            return getOrder;
        } catch (NullPointerException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean checkOrderStatus(Order order) {
        try {
            OrderStatus status = order.getStatus();
            if (status.equals(OrderStatus.PENDING)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateTransaction_OrderFound(Order order, String responseCode, String payDay, String vnp_BankCode) {
        /// return true means SAVE SUCCESS, NO EXCEPTION THROWN
        /// false mean EXCEPTION || STATUS != PENDING
        try {
            long parsedPayday = Long.parseLong(payDay);
            Transaction getTransaction = transactionRepository.findById(order.getOrderId()).get();
            getTransaction.setBankCode(vnp_BankCode);
            getTransaction.setVnpTransactionDate(parsedPayday);
            if (order.getStatus().equals(OrderStatus.PENDING) && getTransaction.getStatus().equals(TransactionStatus.PENDING)) {
                /// both needs to be pending, because
                /// if order cancel, or transaction happen or sth, we dont need to call update
                if (responseCode.equals("00")) {
                    getTransaction.setStatus(TransactionStatus.SUCCESS);
                    transactionRepository.save(getTransaction);
                    return true;
                } else {
                    order.setStatus(OrderStatus.CANCEL);
                    getTransaction.setStatus(TransactionStatus.FAIL);
                    transactionRepository.save(getTransaction);
                    orderRepository.save(order);
                    return true;
                }
            }
            return false;
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public String clientResponseBuilder(Order order, String vnp_txtRef) {
        try {
            Transaction getTransaction = transactionRepository.findByVnpTxnRef(vnp_txtRef);
            String orderStatus = order.getStatus().toString();
            String orderService = order.getService().getServiceName();
            String orderPrice = String.valueOf(order.getPrice());
            String orderTimeSlot = "from " + order.getSchedule().getTimeSlot().getStartTime().toString().trim() + " to "
                    + order.getSchedule().getTimeSlot().getEndTime().toString().trim();
            String orderCreated = order.getCreateTime().toString();
            String transactionBankCode = getTransaction.getBankCode();
            String transactionPaymentMethod = getTransaction.getPaymentMethod();
            String transactionStatus = getTransaction.getStatus().toString();
            Map<String, String> returnParam = new HashMap<>();
            returnParam.put("orderStatus", orderStatus);
            returnParam.put("orderService", orderService);
            returnParam.put("orderPrice", orderPrice);
            returnParam.put("orderTimeSlot", orderTimeSlot);
            returnParam.put("orderCreated", orderCreated);
            returnParam.put("transactionBankCode", transactionBankCode);
            returnParam.put("transactionPaymentMethod", transactionPaymentMethod);
            returnParam.put("transactionStatus", transactionStatus);
            ListIterator<String> keySets = returnParam.keySet().stream().toList().listIterator();
            StringBuffer builderReturnParam = new StringBuffer("");
            while (keySets.hasNext()) {
                String key = keySets.next();
                System.out.print(key);
                String value = returnParam.get(key);
                builderReturnParam.append(key + "=" + value);
                if (keySets.hasNext()) {
                    builderReturnParam.append('&');
                }
            }
            System.out.println(builderReturnParam);
            return builderReturnParam.toString().trim();
        } catch (Exception e) {
            System.out.println("error in response builder");
            return null;
        }
    }

    public void cancelOrder_TransactionWhenServerError(Order order, String vnp_txnRef) {
        try {
            order.setStatus(OrderStatus.CANCEL);
            Transaction getTransaction = transactionRepository.findByVnpTxnRef(vnp_txnRef);
            getTransaction.setStatus(TransactionStatus.FAIL);
            orderRepository.save(order);
            transactionRepository.save(getTransaction);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ResponseEntity<String> queryVNPAY(String vnp_txnRef, long transactionDate, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //Command:querydr
        try {
            String vnp_RequestId = ConfigVNpay.getRandomNumber(8);
            String vnp_Version = "2.1.0";
            String vnp_Command = "querydr";
            String vnp_TmnCode = ConfigVNpay.vnp_TmnCode;
            String vnp_TxnRef = vnp_txnRef;//req.getParameter("order_id");
            String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
            String vnp_TransDate = String.valueOf(transactionDate);//req.getParameter("trans_date");//vnp_payday

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            //20230616094041
            String vnp_IpAddr = ConfigVNpay.getIpAddress(req);
            // 63562614
            JsonObject vnp_Params = new JsonObject();

            vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
            vnp_Params.addProperty("vnp_Version", vnp_Version);
            vnp_Params.addProperty("vnp_Command", vnp_Command);
            vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
            vnp_Params.addProperty("vnp_TransactionDate", vnp_TransDate);
            vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
            vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);
            String hash_Data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" + vnp_TxnRef + "|" + vnp_TransDate + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo;
            String vnp_SecureHash = ConfigVNpay.hmacSHA512(ConfigVNpay.vnp_HashSecret, hash_Data.toString());
            vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);
            URL url = new URL(ConfigVNpay.vnp_apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(vnp_Params.toString());
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            System.out.println("nSending 'POST' request to URL : " + url);
            System.out.println("Post Data : " + vnp_Params);
            System.out.println("Response Code : " + responseCode);
            ////////print message
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String output;
            StringBuffer response = new StringBuffer();
            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();
            ////////print message
            System.out.println(response.toString());
            return ResponseEntity.ok().body(response.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("ERROR SERVER");
        }
    }

    public ResponseEntity<?> refund(String vnp_txnRef,
                                    String transactionDate,
                                    int getAmount,
                                    String user,
                                    HttpServletRequest req,
                                    HttpServletResponse resp) throws IOException {
        String vnp_RequestId = ConfigVNpay.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = ConfigVNpay.vnp_TmnCode;
//        02: Giao dịch hoàn trả toàn phần (vnp_TransactionType=02)
//        03: Giao dịch hoàn trả một phần (vnp_TransactionType=03)

        String vnp_TransactionType = transactionTypeValue;//req.getParameter("vnp_TransactionType")
        String vnp_TxnRef = vnp_txnRef;//req.getParameter("order_id");
        int amount = getAmount * 100;//Integer.parseInt(req.getParameter("amount"))*100;//150000 * 100;10000000
        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = "";
        String vnp_TransactionDate = transactionDate;//String.valueOf(20230616094041l) ;//req.getParameter("trans_date"); //
        String vnp_CreateBy = user;
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = ConfigVNpay.getIpAddress(req);

        JsonObject vnp_Params = new JsonObject();
        //63562614
        //20230616094041
        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_Amount", vnp_Amount);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

        if (vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty()) {
            vnp_Params.addProperty("vnp_TransactionNo", "{get value of vnp_TransactionNo}");
        }

        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

        String hash_Data = vnp_RequestId + "|" + vnp_Version + "|" + vnp_Command + "|" + vnp_TmnCode + "|" +
                vnp_TransactionType + "|" + vnp_TxnRef + "|" + vnp_Amount + "|" + vnp_TransactionNo + "|"
                + vnp_TransactionDate + "|" + vnp_CreateBy + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo;

        String vnp_SecureHash = ConfigVNpay.hmacSHA512(ConfigVNpay.vnp_HashSecret, hash_Data.toString());

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(ConfigVNpay.vnp_apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        System.out.println(response.toString());
        if(responseCode == 200){
            return ResponseEntity.ok().body(response);
        }else{
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    public void saveTransaction_COD(String order_id, String Payment_id) {
        try {
            long parsedOrderId = Long.parseLong(order_id);
            long parsedPaymentId = Long.parseLong(Payment_id);
            Order getOrder = orderRepository.findById(parsedOrderId).get();
            Transaction toSaveTransaction =
                    Transaction.builder()
                            .paymentMethod(paymentRepository.findById(parsedPaymentId).get().getPaymentName().trim())
                            .transactionId(getOrder.getOrderId())
                            .order(getOrder)
                            .status(TransactionStatus.PENDING)
                            .build();
            System.out.println(toSaveTransaction);
            transactionRepository.save(toSaveTransaction);
        } catch (NumberFormatException e) {
            System.out.println("ERROR in VNPAY service: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("ERROR in VNPAY service: " + e.getMessage());
        }

    }

    public Map<Long, String> getAllOrderUrlMap() {
        return this.orderUrlMap;
    }

    public void addOrderUrlMap(Long OrderId, String url) {
        this.orderUrlMap.put(OrderId, url);
    }

    public boolean deleteOrderUrlMapItem(Long orderId) {
        Map<Long, String> getMap = getAllOrderUrlMap();
        try {
            getMap.remove(orderId);
            return true;
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }

    }
////////////////////// get order expried time//////////////////////
    public String getOrderUrlMapByOrderId(Long orderId) {
        Map<Long, String> getMap = getAllOrderUrlMap();
        try {
            String getUrl = getMap.get(orderId);
            if(getUrl == null || getUrl.isBlank()){
                return null;
            }
            return getUrl;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String getCreateTimeFromUrlMap(Long order_Id) {
        String getUrl = getOrderUrlMapByOrderId(order_Id);
        if(getUrl == null || getUrl.isBlank()){
            return null;
        }else {
            String[] getSplitUrl = getUrl.split("&");
            System.out.println(getSplitUrl[5]);
            String getExpiredDate = getSplitUrl[5].split("=")[1];
            return getExpiredDate;
        }
    }
    public boolean checkTransactionExpired(String expiredDate){
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date parseExpiredDate = format.parse(expiredDate);
            Date currentTime  = new Date(System.currentTimeMillis());
            if(parseExpiredDate.before(currentTime)){
                System.out.println("yes transaction expired");
                return true;
            }else{
                System.out.println("no transaction not expired");
                return false;
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }
    ////////////////////// get order expried time//////////////////////
    public String extractResponseCode(String response){
        String[] splitTransaction = response.split(",");
        String getResponseCode = splitTransaction[2].split(":")[1].substring(1,3);
        return getResponseCode;
    }
    public String extractTransactionAmount(String response){
        String[] splitTransaction = response.split(",");
        String getAmount = splitTransaction[6].split(":")[1];
        getAmount = getAmount.substring(1,getAmount.length()-1);
        return getAmount;
    }
    public String extractTransactionStatusQuery(String responseQuery){
        String[] splitTransaction = responseQuery.split(",");
        String getTransactionStatus = splitTransaction[12].split(":")[1].substring(1,3);
        return getTransactionStatus;
    }
    public String extractTransactionPayDateQuery(String responseQuery){
        String[] splitTransaction = responseQuery.split(",");
        String getPaydate = splitTransaction[9].split(":")[1];
        getPaydate = getPaydate.substring(1,getPaydate.length()-1);
        return getPaydate;
    }

}



