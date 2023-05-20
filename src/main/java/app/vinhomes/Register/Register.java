package app.vinhomes.Register;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register {
    public static boolean checkUsername(String username){
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]{3,15}$");
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();

    }
    public static boolean checkPassword(String password){
        return ;
    }
    public static boolean checkEmail(){

    }
    public static boolean checkFirstname(){

    }
    public static boolean checkLastname(){

    }
    public static boolean checkAddress(){

    }
}
