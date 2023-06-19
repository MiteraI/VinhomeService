package app.vinhomes.vnpay.controller;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import app.vinhomes.vnpay.config.ConfigVNpay;


import javax.xml.crypto.dsig.SignatureMethod;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import java.util.TimeZone;

@RestController
@RequestMapping(value = "/vnpay/refund")
public class VNpayRefund {
    private final String transactionTypeValue = "02";

    @GetMapping("/getlong")
    public String getLongDate() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        Date date = format.parse("11/06/2023 23:03:18");
        SimpleDateFormat anotherFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String returnString = anotherFormat.format(date);
        System.out.println(returnString);
        return returnString;
    }

    @GetMapping
    public ResponseEntity<?> refund(HttpServletRequest req,  HttpServletResponse resp) throws IOException {
        String vnp_RequestId = ConfigVNpay.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = ConfigVNpay.vnp_TmnCode;
//        02: Giao dịch hoàn trả toàn phần (vnp_TransactionType=02)
//        03: Giao dịch hoàn trả một phần (vnp_TransactionType=03)
        // mac dinh = 2, dell ai tra mot phan cho met

        String vnp_TransactionType = transactionTypeValue ;//req.getParameter("vnp_TransactionType")
            String vnp_TxnRef = "93698609";//req.getParameter("order_id");
        // response từ query trả về từ vnpay ko cần *100, nó đã sẵn nhân 100 rồi
            int amount =15000000 ;//Integer.parseInt(req.getParameter("amount"))*100;//150000 * 100;10000000
        String vnp_Amount = String.valueOf(amount);
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;
        String vnp_TransactionNo = "";
            String vnp_TransactionDate =String.valueOf(20230611230318l) ;//req.getParameter("trans_date"); //
        String vnp_CreateBy = "adsasdsad";//req.getParameter("user");NGUYEN VAN A// ko quan trong

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        String vnp_IpAddr = ConfigVNpay.getIpAddress(req);

        JsonObject vnp_Params = new JsonObject ();

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

        if(vnp_TransactionNo != null && !vnp_TransactionNo.isEmpty())
        {
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

        URL url = new URL (ConfigVNpay.vnp_apiUrl);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
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
        return ResponseEntity.ok().body("yes refund success maybe");
    }
}
