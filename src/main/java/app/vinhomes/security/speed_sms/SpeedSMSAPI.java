package app.vinhomes.security.speed_sms;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.bind.DatatypeConverter;
public class SpeedSMSAPI {
        public static final String API_URL = "https://api.speedsms.vn/index.php";
        protected String mAccessToken;

        public SpeedSMSAPI(String accessToken) {
            this.mAccessToken = accessToken;
        }

        /**
         * Get user information
         * @param: none
         * @return: json string
         * */
        public String getUserInfo() throws IOException {
            URL url = new URL(API_URL + "/user/info");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            String userCredentials = mAccessToken + ":x";
            String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userCredentials.getBytes());
            conn.setRequestProperty ("Authorization", basicAuth);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine = "";
            StringBuffer buffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
            }
            in.close();
            return buffer.toString();
        }

        /**
         * Send SMS
         * @param
         * @return: json string
         * */
        public String sendSMS(String to, String content, int type, String sender) throws IOException {
            String json = "{\"to\": [\"" + to + "\"], \"content\": \"" + EncodeNonAsciiCharacters(content) + "\", \"type\":" + type + ", \"brandname\":\"" + sender + "\"}";
            URL url = new URL(API_URL + "/sms/send");
            System.out.println(url ); System.out.println(json);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            String userCredentials = mAccessToken + ":x";
            String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(userCredentials.getBytes());
            conn.setRequestProperty ("Authorization", basicAuth);
            conn.setRequestProperty("Content-Type", "application/json");
            System.out.println("now send speed sms");
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(json);
            wr.flush();
            wr.close();
            System.out.println("now receive input");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine = "";
            StringBuffer buffer = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                buffer.append(inputLine);
            }
            in.close();
            System.out.println("now finish sending");
            return buffer.toString();
        }

        private String EncodeNonAsciiCharacters(String value) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                int unit = (int)c;
                if (unit > 127) {
                    String hex = String.format("%04x", (int)unit);
                    String encodedValue = "\\u" + hex;
                    sb.append(encodedValue);
                }
                else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }


}
