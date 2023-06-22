package app.vinhomes.vnpay.controller;


import app.vinhomes.entity.Transaction;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.service.OrderService;
import app.vinhomes.service.PaymentService;
import app.vinhomes.service.TransactionService;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.vinhomes.vnpay.config.ConfigVNpay;
import app.vinhomes.vnpay.service.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/vnpay/createPayment")
public class VNpayController extends HttpServlet {
    @Autowired

    private VNPayService vnpayService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    private String errorURL = "/";
    @CrossOrigin//origins = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html")
    @PostMapping//@RequestParam JsonNode jsonNode ,
    public ResponseEntity<String> createPayment(@RequestBody JsonNode jsonNode , HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "billpayment";
        JsonNode jsonNode1 ;
        ////////////////////////////////////////////////////////////
        String orderID = null;

        String transactionMethodID = jsonNode.get("paymentId").asText();//req.getParameter("transactionMethod");//
        String dayfromFormOrderService= jsonNode.get("day").asText();//req.getParameter("day") ;
        if( transactionMethodID !=""){//transactionMethodID != null ||transactionMethodID.equals("")
            ResponseEntity<String> response = orderService.createOrder(jsonNode,req);// this return resp status + ORDER ID!!
            if(response.getStatusCode().is2xxSuccessful())    {
                System.out.println(response.getBody());
                orderID =response.getBody();
            }else{
                System.out.println("order id is not assigned");
                System.out.println(response.getBody());
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("fail to aquire orderID") ;
            }
        }else{
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("choose your payment method"  );

        }
        ////////////////////////////////////////////////////////////////
        int amount =(int) vnpayService.getServicePriceFromOrder(orderID)* 100;
        if(amount <= 0){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("invalid money ");

        }//////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////
        String bankCode = vnpayService.getBankCode_CheckCOD_VNpay(jsonNode,req);        //String bankCode = "VNBANK";

        if( bankCode == null){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("bankcode is empty, this will be fixed later") ;
            //TODO quan li neu chon thanh toan COD thay vi VNPAY
        }else if(bankCode.isEmpty()){// this mean this is COD, not vnpay, so we dont have to redirect it to vnpay site

            vnpayService.saveTransaction_COD(orderID,transactionMethodID);

            return ResponseEntity.ok().body("pay by COD, procede to go back main page");
            //TODO neu la COD thi lam gi tiep, ko tiep tuc gui thong tin cho vnpay
        }
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
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
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

        cld.add(Calendar.MINUTE, 15);
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

        if(getTransactionObj != null){
            System.out.println("  "+ getTransactionObj.getVnpTxnRef());
        }
        //////////////////////////////////////////////////////////////////////////////////////////
        //resp.getWriter().write(gson.toJson(job));
//        vnpayService.redirectTest(resp,paymentUrl);
        //resp.sendRedirect(paymentUrl);
        //RedirectView redirectView = new RedirectView();
        //redirectView.setUrl(payreturn redirectView;
        return ResponseEntity.ok().body(paymentUrl.toString().trim());

    }


}
