package app.vinhomes.vnpay.controller;


import app.vinhomes.entity.Order;
import app.vinhomes.repository.OrderRepository;
import app.vinhomes.vnpay.service.VNPayService;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import app.vinhomes.vnpay.config.*;


import static java.lang.System.out;

@Controller
@RequestMapping(value = "/vnpay/returnurl")
public class VNpayReturnURL {
    @Autowired
    private ConfigVNpay config;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VNPayService VNpayService;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public String getReturnUrl(HttpServletRequest request) {
        try {
            out.println("inside return url");

            Map fields = new HashMap();
            JsonObject json = new JsonObject();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);

                    out.println(fieldName + "               " + fieldValue);

                }
            }
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            String signValue = config.hashAllFields(fields);

            String responseCode = request.getParameter("vnp_ResponseCode");
            String payDay = request.getParameter("vnp_PayDate");
            String bankCode = request.getParameter("vnp_BankCode");
            String vnp_txnRef = request.getParameter("vnp_TxnRef");
            Order getOrder = VNpayService.getOrderByVnp_txnRef(vnp_txnRef);
            if (signValue.equals(vnp_SecureHash)) {
                boolean checkOrderStatus = false;
                if (getOrder != null) {
                    checkOrderStatus = VNpayService.checkOrderStatus(getOrder);
                }
                if (checkOrderStatus) {
                    if ("00".equals(responseCode)) {
                        //Here Code update PaymnentStatus = 1 into your Database
                        VNpayService.updateTransaction_OrderFound(getOrder, responseCode, payDay, bankCode);
                        VNpayService.deleteOrderUrlMapItem(getOrder.getOrderId());
                        String responseInfo = VNpayService.clientResponseBuilder(getOrder,vnp_txnRef);
                        if(responseInfo != null){
                            return "redirect:/transactionReturn?message=success transaction&"+responseInfo;
                        }
                        return "redirect:/transactionReturn?message=success transaction&error=cant find order and transaction";
                    } else {
                        VNpayService.updateTransaction_OrderFound(getOrder, responseCode, payDay, bankCode);
                        VNpayService.deleteOrderUrlMapItem(getOrder.getOrderId());
                        String responseInfo = VNpayService.clientResponseBuilder(getOrder,vnp_txnRef);
                        if(responseInfo != null){
                            return "redirect:/transactionReturn?message=order cancel&"+responseInfo;
                        }
                        // Here Code update PaymnentStatus = 2 into your Database
                        return "redirect:/transactionReturn?message=order cancel&error=cant find order and transaction";
                    }
                } else {
                    VNpayService.updateTransaction_OrderFound(getOrder, responseCode, payDay, bankCode);
                    VNpayService.deleteOrderUrlMapItem(getOrder.getOrderId());
                    String responseInfo = VNpayService.clientResponseBuilder(getOrder,vnp_txnRef);
                    if(responseInfo != null){
                        return "redirect:/transactionReturn?message=order status is not PENDING&"+responseInfo;
                    }
                    return "redirect:/transactionReturn?message=order status is not PENDING&error=cant find order and transaction";
                }
            } else {
                out.println("Chu ky khong hop le");
                VNpayService.updateTransaction_OrderFound(getOrder,responseCode,payDay,bankCode);
                VNpayService.deleteOrderUrlMapItem(getOrder.getOrderId());
                String responseInfo = VNpayService.clientResponseBuilder(getOrder,vnp_txnRef);
                if(responseInfo != null){
                    return "redirect:/transactionReturn?message=transaction key is not match with secret key," +
                            " someone is interrupting transaction&"+responseInfo;
                }
                return "redirect:/transactionReturn?message=transaction key is not match with secret" +
                        " key, someone is interrupting transaction&error=cant find order and transaction";
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            return "redirect:/transactionReturn?message=error on serverside, please make another order";
        }
    }
}
