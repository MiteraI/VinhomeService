package app.vinhomes.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class CheckSpecialChar {
        public static boolean isValidFileName(String fileName) {
            Pattern pattern = Pattern.compile("^[A-Za-z][A-Za-z0-9-]*$");
            Matcher matcher = pattern.matcher(fileName);
            return matcher.matches();
        }
}
