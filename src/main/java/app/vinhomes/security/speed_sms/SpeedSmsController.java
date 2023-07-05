package app.vinhomes.security.speed_sms;

import com.azure.core.annotation.Post;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(value = "/speedsms")
public class SpeedSmsController {
    private final String accessToken = "ef_4sDm1PIiI6GSAF_Lx3iXbTV593Zts";
    @PostMapping
    public void sendSMS() throws IOException {
        /** Đầu tiên bạn tạo một instance của class SpeedSMSAPI với tham số là api ccess token của bạn.
         */
        SpeedSMSAPI api  = new SpeedSMSAPI(accessToken);
/**
 * Để lấy thông tin về tài khoản như: email, số dư tài khoản bạn sử dụng hàm getUserInfo()
 */
//        String userInfo = api.getUserInfo();
/* * Hàm getUserInfo() sẽ trả về một json như sau:
 * /
{"email": "test@speedsms.vn", "balance": 100000.0, "currency": "VND"}

/** Để gửi SMS bạn sử dụng hàm sendSMS như sau:
*/
        String phone = "0847942496";
        String content = "test sms";
        int type = 2;
/**
 sms_type có các giá trị như sau:
 2: tin nhắn gửi bằng đầu số ngẫu nhiên
 3: tin nhắn gửi bằng brandname
 4: tin nhắn gửi bằng brandname mặc định (Verify hoặc Notify)
 5: tin nhắn gửi bằng app android
 */
        String sender = "";//0847942496
/**
 brandname là tên thương hiệu hoặc số điện thoại đã đăng ký với SpeedSMS hoặc android deviceId của bạn
 */

        String response = api.sendSMS(phone, content, type, sender);
        System.out.println(response);
/**hàm sendSMS sẽ trả về một json string như sau:*/
//        {
//            "status": "success", "code": "00",
//                "data": {
//            "tranId": 123456, "totalSMS": 1,
//                    "totalPrice": 250, "invalidPhone": []
//        }
//        }

// Trong trường hợp gửi sms bị lỗi, hàm sendSMS sẽ trả về json string như sau:
//        {
//            "status": "error", "code": "error code", "message" : "error description",
//                "invalidPhone": ["danh sách sdt lỗi"]
//        }
    }
}
