package app.vinhomes.vnpay.controller;


import app.vinhomes.entity.Account;
import app.vinhomes.entity.Order;
import app.vinhomes.entity.Transaction;
import app.vinhomes.event.event_storage.StartOrderCountDown;
import app.vinhomes.event.listener_storage.OnCreateOrder;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.security.SecurityService;
import app.vinhomes.service.OrderService;
import app.vinhomes.service.PaymentService;
import app.vinhomes.service.ServiceTypeService;
import app.vinhomes.service.TransactionService;

import com.azure.core.annotation.Get;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import app.vinhomes.vnpay.config.ConfigVNpay;
import app.vinhomes.vnpay.service.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/vnpay/createPayment")
public class VNpayController extends HttpServlet {
    @Value("${time.order_timeout}")
    private int ORDER_TIMEOUT;
    @Value("${order.policy.day_before_service}")
    private String DAY_POLICY_ORDER;
    @Value("${order.policy.hour_before_service}")
    private String HOUR_POLICY_ORDER;
    @Value("${order.policy.max_day_prior_to_service}")
    private String DAY_PRIOR_ORDER;
    @Autowired
    private OrderService orderService;
    @Value(value = "${order.policy.max_order_a_day}")
    private int ORDER_MAX;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private VNPayService vnpayService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private ServiceTypeService serviceTypeService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @PostMapping//@RequestParam JsonNode jsonNode ,
    public ResponseEntity<String> createPayment(@RequestBody JsonNode jsonNode, Authentication authentication, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "billpayment";
        String orderID = null;
        String getDay = jsonNode.get("day").asText().trim();
        String getTimeId= jsonNode.get("timeId").asText().trim();
        String getPaymentId= jsonNode.get("paymentId").asText().trim();
        String phonenumber = jsonNode.get("phonenumberId").asText().trim();
        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////
        System.out.println("Inside vnpay " + authentication.getName());
        boolean checking = securityService.checkIfEnabledFromAuthentication(authentication);
        if (checking) {
            System.out.println("yes this account has been enabled");
        } else {
            System.out.println("this account has not been enabled");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("not yet enable account");
        }
        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////
        if(getDay.equals("0")||getTimeId.equals("0")||getPaymentId.equals("0")||getDay.equals("0")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR please enter all fields");
        }
        if(orderService.checkDayValidForOrder(getDay,getTimeId) == false){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR inappropriate timeslot, rules are\n " +
                    "1. order has to be made atlease "+DAY_POLICY_ORDER+ " before\n" +
                    "2. If in the same day or tomorrow, then atleast "+HOUR_POLICY_ORDER+ " early\n" +
                    "3. order must not be placed after "+ DAY_PRIOR_ORDER+" days count from current day");
        }
        Account getAccount = securityService.getUserThroughAuth(authentication);
        if(orderService.checkIfReachMaxOrder(getAccount.getAccountId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR reach max "+ ORDER_MAX+" order a day");
        }
        if(phonenumber.equals("")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR phonenumber is empty, please re-login and try again");
        }
        String dayfromFormOrderService = jsonNode.get("day").asText();//req.getParameter("day") ;
        if(dayfromFormOrderService.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR day is empty, please try again");
        }
        String transactionMethodID = jsonNode.get("paymentId").asText();//req.getParameter("transactionMethod");//

        if (transactionMethodID != "") {//transactionMethodID != null ||transactionMethodID.equals("")
            ResponseEntity<String> response = orderService.createOrder(jsonNode, req);// this return resp status + ORDER ID!!
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println(response.getBody());
                orderID = response.getBody();
            } else {
                System.out.println("order id is not assigned");
                System.out.println(response.getBody());
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response.getBody());
            }
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("choose your payment method");
        }
        int amount = (int) vnpayService.getServicePriceFromOrder(orderID) * 100;
        if (amount <= 0) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("invalid money ");

        }
        String bankCode = vnpayService.getBankCode_CheckCOD_VNpay(jsonNode, req);        //String bankCode = "VNBANK";
        if (bankCode == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("bankcode is empty, this will be fixed later");
        } else if (bankCode.isEmpty()) {// this mean this is COD, not vnpay, so we dont have to redirect it to vnpay site
            vnpayService.saveTransaction_COD(orderID, transactionMethodID);
            return ResponseEntity.ok().body("COD");
        }
        Long getServiceId = jsonNode.get("serviceId").asLong();
        String getServiceName = serviceTypeService.getServiceByServiceId(getServiceId).getServiceName();

        String vnp_TxnRef = ConfigVNpay.getRandomNumber(8);
        String vnp_IpAddr = ConfigVNpay.getIpAddress(req);
        String vnp_TmnCode = ConfigVNpay.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" +getServiceName );//vnp_TxnRef
        vnp_Params.put("vnp_OrderType", orderType);
        String locate = null;//jsonNode.get("language").asText();
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", ConfigVNpay.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, ORDER_TIMEOUT);
        //cld.add(Calendar.SECOND, 15l);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = ConfigVNpay.hmacSHA512(ConfigVNpay.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = ConfigVNpay.vnp_PayUrl + "?" + queryUrl;
        JsonObject job = new JsonObject();
        job.addProperty("code", "00");
        job.addProperty("message", "success");
        job.addProperty("data", paymentUrl);
        Gson gson = new Gson();
        System.out.println(paymentUrl);
        /////////////////////////////////////////////DATABASE SAVE TRANSACTION AND STATUS//////////////////////////////////////////////////
        Transaction getTransactionObj = vnpayService.BuildTransactionThroughOrder(
                Long.parseLong(orderID),
                vnp_TxnRef,
                transactionMethodID);
        Order getOrderObj = null;
        if (getTransactionObj != null) {
            getOrderObj = getTransactionObj.getOrder();
            vnpayService.addOrderUrlMap(getOrderObj.getOrderId(),paymentUrl);
            eventPublisher.publishEvent(new StartOrderCountDown(LocalDateTime.now(), getTransactionObj.getTransactionId()));
        }
        //////////////////////////////////////////////////////////////////////////////////////////
        //resp.getWriter().write(gson.toJson(job));
//        vnpayService.redirectTest(resp,paymentUrl);
        return ResponseEntity.ok().body(paymentUrl.toString().trim());

    }


    @PostMapping(value = "/continuePaying/{orderId}",produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> repayOrder(@PathVariable String orderId, Authentication authentication){
        try{
            System.out.println("inside repay");
            System.out.println(authentication.getPrincipal());
            System.out.println(orderId);
            Long parsedOrderId = Long.parseLong(orderId);
            Order getOrder = orderService.getOrderById(parsedOrderId);
            if(getOrder.getPayment().getPaymentName().equals("COD")){
                System.out.println("this order is of COD type, cannot continue to pay");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("your order is off COD type");
            }
            boolean checkIfOrderPending_Exist = orderService.checkIfOrderIsPending_IsExist(parsedOrderId);
            boolean checkIfTransactionPending_Exist = transactionService.checkIfTransactionIsPending_IsExist(parsedOrderId);
            System.out.println(checkIfOrderPending_Exist +"   "+ checkIfTransactionPending_Exist);
            if(checkIfOrderPending_Exist == false || checkIfTransactionPending_Exist == false){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("your order is already paid or has another payment method");
            }
            Map<Long,String> getMap  = vnpayService.getAllOrderUrlMap();
            getMap.forEach((Id,url) -> System.out.println(Id+ "  :  "+ url));
            String getUrl = getMap.get(Long.parseLong(orderId));
            if(getUrl == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("url not found for this order ");
            }
            return ResponseEntity.ok().body(getUrl);
        }catch (NumberFormatException e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("order id is not a number ");
        }
    }


}
