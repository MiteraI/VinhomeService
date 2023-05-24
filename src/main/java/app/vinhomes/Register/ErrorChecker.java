package app.vinhomes.Register;

import app.vinhomes.entity.Account;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
public class ErrorChecker {
    @Autowired
    private AccountRepository accountRepository ;
    @Autowired
    private AccountService accountService;
    public  String checkUsername(String username) {
        Account account = accountRepository.findByAccountName(username);
        System.out.println(account);
        if(username.isEmpty()){
            return "please enter username";
        }
        if (username.length()<3 || username.length()>15) {
            return "username can only be between 3 and 15 character long";
        }
        else{
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]{3,15}$");
            Matcher matcher = pattern.matcher(username.trim());
            if(account != null){
                return "this username has already been taken";
            }
            if(matcher.matches()){
                return "";
            }
            else{
                return "wrong format username";
            }
        }
    }

    public  String checkPassword(String password) {
        if(password.isEmpty()){
            return "please enter password";
        }

        Pattern pattern = Pattern.compile("^[\\d\\D]{3,15}$");
        Matcher matcher = pattern.matcher(password.trim());
        if(matcher.matches()){
            return "";
        }else{
            return "password should contain special character and stuff";
        }

    }

    public String checkEmail(String email) {

        Account account  = accountRepository.findByEmailEquals(email);
        System.out.println(account);
        if(email.isEmpty()){
            return "please enter your email";
        }
        Pattern pattern1 = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}+\\.[a-zA-Z]{2,6}$");
        Matcher matcher1 = pattern1.matcher(email.trim());
        Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}$");
        Matcher matcher2 = pattern2.matcher(email.trim());
        if(account != null){
            System.out.println(account);
            return "email have already been used";
        }
        else if (matcher1.matches()) {
            return "";
        } else if (matcher2.matches()) {
            return "";
        } else {
            return "enter the correct email";
        }
    }

    public  String checkFirstname(String firstname) {
        if(firstname.isEmpty()){
            return "please enter your first name";
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z\\s]{3,15}$");
        Matcher matcher = pattern.matcher(firstname.trim());
        if(matcher.matches()){
            return "";
        }else{
            return "please enter your name, is should not have any special character";
        }

    }

    public  String checkLastname(String lastname) {
        if(lastname.isEmpty()){
            return "please enter your last name";
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z\\s]{3,15}$");
        Matcher matcher = pattern.matcher(lastname.trim());
        if(matcher.matches()){
            return "";
        }else{
            return "please enter your name, is should not have any special character";
        }
    }
    public  String checkDate(String date){
        if(date.isEmpty()){
            return "please enter your Date of Birth";
        }else{
            return "";
        }
    }

    public  String checkAddress(String block, String roomnumber) {
        //block ko can phai kiem tra, vi van de ko phai block bi lam gia hay doi, do no xai RADIO button, ko can nhap nen ko sai
        // but co kha nang bi trun RoomNumber trong cung 1 block, nene se query lay bang do ra trc de check trung
        try {
            int roomNo = Integer.parseInt(roomnumber);
            /// can xem lai ve quy luat so phong
            if (roomNo > 100 && roomNo < 999) {
                return "";
            } else {
                return "your room number is not correct, enter your real room";
            }
        } catch (NumberFormatException e) {
            //System.out.println(e);
            return "please enter room number, it should not contain character";
        }

    }

    public  String checkPhoneNumber(String phonenumber) {
        //phone number regrex VAN CHUA HOAN THIEN< CAN XEM LAI CHO DUNG
        try{
            Pattern pattern = Pattern.compile("^\\d{10,12}$");
            Matcher matcher = pattern.matcher(phonenumber);
            if(matcher.matches()){
                return "";
            }else{
                return "your phone number should contain only number between 10 and 12 ";
            }
        }catch(NumberFormatException e){
            //System.out.println(e);
            return "phone should only contain number";
        }

    }
    public  String checkStatus(String status){
        if(status.isEmpty()){
            return "status is empty";
        }
        try{
            int statupdate = Integer.parseInt(status);
            return "";
        }catch (NumberFormatException e){
            System.out.println(e);
            return "the status is not a number 0 or 1, try again";
        }
    }
}

//    public static void main(String[] args) {
//        System.out.println(checkEmail("abc.d@gmail.com.bbb"));
//        System.out.println(checkEmail("2asdf2@gmail.com."));
//        System.out.println(checkEmail("asdfasdf324323423421"));
//    }

//public static void main(String[] args) {
//    System.out.println(checkFirstname("abc"));
//    System.out.println(checkFirstname("ab c"));
//    System.out.println(checkFirstname("abc asdf asdf"));
//}

