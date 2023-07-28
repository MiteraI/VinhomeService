package app.vinhomes.security.esms.otp_service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;

import app.vinhomes.common.ErrorChecker;
import app.vinhomes.security.esms.otp_dto.OTPAttribute;
import app.vinhomes.security.esms.otp_service.OTPService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ESMSservice {
    @Autowired
    private OTPService otpService;
    private final String APIKey="6F068A8F88779614D0670C292374E8";//9F7FFB88511825A91061978CE826DC
    //Dang ky tai khoan tai esms.vn de lay Key
    private final String SecretKey="D659ACFC8A0DA986C84EA1B47C4924";//8BA185CC5367278187D45C6D963139
    public String execute() {
        return "SUCCESS";
    }
    public String sendGetJSON(String phonenumber,String message) throws IOException {
        String phone = checkIfLegitPhonenumber(phonenumber).trim();
        System.out.println(phone);
        if(checkIfLegitPhonenumber(phonenumber).startsWith("ERROR")){
            return null;
        }
        Random random = new Random();
        int requestId = random.nextInt(6);
        String url = "http://rest.esms.vn/MainService.svc/json/SendMultipleMessage_V4_get?ApiKey="
                + URLEncoder.encode(APIKey, "UTF-8")
                + "&SecretKey=" + URLEncoder.encode(SecretKey, "UTF-8")
                + "&SmsType=8&Phone=" + URLEncoder.encode(phone, "UTF-8")
                + "&Content=" + URLEncoder.encode(message, "UTF-8");
//

        URL obj;
        try {
            obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //you need to encode ONLY the values of the parameters
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            if(responseCode==200)//Ä�Ã£ gá»�i URL thÃ nh cÃ´ng, tuy nhiÃªn báº¡n pháº£i tá»± kiá»ƒm tra CodeResult xem tin nháº¯n cÃ³ gá»­i thÃ nh cÃ´ng khÃ´ng, vÃ¬ cÃ³ thá»ƒ tÃ i khoáº£n báº¡n khÃ´ng Ä‘á»§ tiá»�n thÃ¬ sáº½ tháº¥t báº¡i
            {
                System.out.println("send success");
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JsonObject json =  (JsonObject) new JsonParser().parse(response.toString());
            System.out.println("CodeResult=" + json.get("CodeResult"));
            System.out.println("SMSID=" + json.get("SMSID"));
            System.out.println("ErrorMessage=" + json.get("ErrorMessage"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return "SUCCESS";
    }

    public Map<String, OTPAttribute> getOTPmap(){
        return otpService.getOTPMap();
    }
    private String checkIfLegitPhonenumber(String phonenumber){
        try{
            if(phonenumber == null || phonenumber.isBlank()){
                return "ERROR_PHONE_EMPTY";
            }
            if(phonenumber.length() != 10 ){
                return "ERROR_PHONE_LENGTH";
            }
            return phonenumber;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return "ERROR_SERVER";
        }
    }
}
