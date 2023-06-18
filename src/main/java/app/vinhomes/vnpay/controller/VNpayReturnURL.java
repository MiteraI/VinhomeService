package app.vinhomes.vnpay.controller;

import app.vinhomes.repository.OrderRepository;
import com.google.gson.JsonObject;
import com.twilio.http.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import app.vinhomes.vnpay.config.*;
import org.springframework.web.servlet.function.ServerResponse;

import javax.print.attribute.standard.Media;

import static java.lang.System.out;

@RestController
@RequestMapping(value = "/vnpay/returnurl")
public class VNpayReturnURL {
    @Autowired
    private ConfigVNpay config;
    @Autowired
    private OrderRepository orderRepository;


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReturnUrl(HttpServletRequest request) {
        try {
            out.println("inside return url");
            //Begin process return from VNPAY
            Map fields = new HashMap();
            JsonObject json = new JsonObject();
            for (Enumeration params = request.getParameterNames(); params.hasMoreElements(); ) {
                String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                    out.println(fieldName + "               "+ fieldValue);
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
            out.println(signValue);
            if (signValue.equals(vnp_SecureHash)) {
                //vnp_TxnRef
                String vnp_txnRef = request.getParameter("vnp_TxnRef");
                String amount = request.getParameter("vnp_Amount");
                String status ; // kiem don hang, muon lam cung dc, ko lam cung dc
                boolean checkOrderId =true ;
                boolean checkAmount =true ;
                boolean checkOrderStatus = true;
                if (checkOrderId) {
                    if (checkAmount) {
                        if (checkOrderStatus) {
                            if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
                                //Here Code update PaymnentStatus = 1 into your Database
                                out.println("yea stuff work");
                                return ResponseEntity.ok().body("yes stuff work");
                            } else {
                                // Here Code update PaymnentStatus = 2 into your Database
                            }
                            out.print("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
                        } else {
                            out.print("{\"RspCode\":\"02\",\"Message\":\"Order already confirmed\"}");
                        }
                    } else {
                        out.print("{\"RspCode\":\"04\",\"Message\":\"Invalid Amount\"}");
                    }
                } else {
                    out.print("{\"RspCode\":\"01\",\"Message\":\"Order not Found\"}");
                }
//                if ("00".equals(request.getParameter("vnp_ResponseCode"))) {
//
//                    out.println("GD Thanh cong");
//                    return ResponseEntity.status(HttpStatus.OK).body("Yea stuff seem to work");
//                } else {
//                    out.println("GD Khong thanh cong");
//                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("gd ko thanh cong");
//                }
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
            } else {
                out.println("Chu ky khong hop le");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("chu ky ko hop le");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
