package app.vinhomes.vnpay.controller;

import app.vinhomes.vnpay.config.ConfigVNpay;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

@RestController
@RequestMapping(value = "/vnpay/ipn")
public class VNpayIPN {
    @GetMapping
    public String laydulieutuVnpayChoAdmin(HttpServletRequest request) throws UnsupportedEncodingException {
        try {
            out.println("inside ipn url");
            /*  IPN URL: Record payment results from VNPAY
            Implementation steps:
            Check checksum
            Find transactions (vnp_TxnRef) in the database (checkOrderId)
            Check the payment status of transactions before updating (checkOrderStatus)
            Check the amount (vnp_Amount) of transactions before updating (checkAmount)
            Update results to Database
            Return recorded results to VNPAY
            */
            // ex:  	    PaymnentStatus = 0; pending
            //              PaymnentStatus = 1; success
            //              PaymnentStatus = 2; Faile
            //Begin process return from VNPAY
            Map fields = new HashMap();
            JsonObject json = new JsonObject();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            // Check checksum
            String signValue = ConfigVNpay.hashAllFields(fields);
            if (signValue.equals(vnp_SecureHash)) {
                boolean checkOrderId = true; // vnp_TxnRef exists in your database
                boolean checkAmount = true; // vnp_Amount is valid (Check vnp_Amount VNPAY returns compared to the amount of the code (vnp_TxnRef) in the Your database).
                boolean checkOrderStatus = true; // PaymnentStatus = 0 (pending)
                if (checkOrderId) {
                    if (checkAmount) {
                        if (checkOrderStatus) {
                            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                                //Here Code update PaymnentStatus = 1 into your Database
                            } else {
                                // Here Code update PaymnentStatus = 2 into your Database
                            }
                            out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                            return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
                        } else {
                            out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                            return "{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}";
                        }
                    } else {
                        out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                        return "{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}";
                    }
                } else {
                    out.print("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
                    return "{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}";
                }
            } else {
                out.print("{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}");
                return "{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}";
            }
        } catch (Exception e) {
            out.print("{\"RspCode\":\"99\",\"Message\":\"Unknow error\"}");
            return "{\"RspCode\":\"99\",\"Message\":\"Unknow error\"}";
        }
    }
}


