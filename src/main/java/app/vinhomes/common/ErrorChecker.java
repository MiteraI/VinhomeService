package app.vinhomes.common;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.repository.AccountRepository;
import app.vinhomes.repository.customer.AddressRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@org.springframework.stereotype.Service
public class ErrorChecker {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PhoneRepository phoneRepository;

    public String checkUsername(String username) {
        Account account = accountRepository.findByAccountName(username);
        System.out.println(account);
        if (username.isEmpty()) {
            return "please enter username";
        }
        if (username.length() < 3 || username.length() > 15) {
            return "username can only be between 3 and 15 character long";
        } else {
            Pattern pattern = Pattern.compile("^[a-zA-Z0-9._-]{3,15}$");
            Matcher matcher = pattern.matcher(username.trim());
            if (account != null) {
                return "this username has already been taken";
            }
            if (matcher.matches()) {
                return "";
            } else {
                return "wrong format username";
            }
        }
    }

    public String checkPassword(String password) {
        if (password.isEmpty()) {
            return "please enter password";
        }
        Pattern pattern = Pattern.compile("^[\\d\\D]{3,15}$");
        Matcher matcher = pattern.matcher(password.trim());
        if (matcher.matches()) {
            return "";
        } else {
            return "ERROR password should contain 3-15 character";
        }

    }

    public String checkEmail(String email) {

        Account account = accountRepository.findByEmailEquals(email);
        System.out.println(account);
        if (email.isEmpty()) {
            return "please enter your email";
        }
        Pattern pattern1 = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}+\\.[a-zA-Z]{2,6}$");
        Matcher matcher1 = pattern1.matcher(email.trim());
        Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}$");
        Matcher matcher2 = pattern2.matcher(email.trim());
        if (account != null) {
            System.out.println(account);
            return "email have already been used";
        } else if (matcher1.matches()) {
            return "";
        } else if (matcher2.matches()) {
            return "";
        } else {
            return "enter the correct email";
        }
    }

    public String checkFirstname(String firstname) {
        if (firstname.isEmpty()) {
            return "please enter your first name";
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z\\s]{3,15}$");
        Matcher matcher = pattern.matcher(firstname.trim());
        if (matcher.matches()) {
            return "";
        } else {
            return "please enter your name, is should not have any special character";
        }

    }

    public String checkLastname(String lastname) {
        if (lastname.isEmpty()) {
            return "please enter your last name";
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z\\s]{3,15}$");
        Matcher matcher = pattern.matcher(lastname.trim());
        if (matcher.matches()) {
            return "";
        } else {
            return "please enter your name, is should not have any special character";
        }
    }

    public String checkDate(String date) {
        if (date.isEmpty()) {
            return "please enter your Date of Birth";
        } else {
            return "";
        }
    }

    public String checkAddress(String block, String roomnumber) {
        //block ko can phai kiem tra, vi van de ko phai block bi lam gia hay doi, do no xai RADIO button, ko can nhap nen ko sai
        // but co kha nang bi trun RoomNumber trong cung 1 block, nene se query lay bang do ra trc de check trung
        try {
            int roomNo = Integer.parseInt(roomnumber);
            /// can xem lai ve quy luat so phong
            if (roomNo > 100 && roomNo < 999) {
                Address address = addressRepository.findByBuildingBlockAndBuildingRoom(block, roomnumber);
                if (address == null) {
                    return "";
                } else {
                    return "someone has already take this address";
                }

            } else {
                return "your room number is not correct, enter your real room";
            }
        } catch (NumberFormatException e) {
            //System.out.println(e);
            return "please enter room number, it should not contain character";
        }

    }

    public String checkPhoneNumber(String phonenumber) {
        //phone number regrex VAN CHUA HOAN THIEN< CAN XEM LAI CHO DUNG
        try {
            Pattern pattern = Pattern.compile("^\\d{10,12}$");
            Matcher matcher = pattern.matcher(phonenumber);
            List<Phone> phoneList = new ArrayList<>();
            phoneList = phoneRepository.findByNumber(phonenumber);
            if (phoneList.isEmpty() == false) {
                return "this phone number has already been taken, try other number";
            }else if(phonenumber.startsWith("0") == false){
                return "Phone number must start with 0";
            }
            else if (matcher.matches()) {
                return "";
            } else {
                return "your phone number should contain only number between 10 and 12 ";
            }
        } catch (NumberFormatException e) {
            //System.out.println(e);
            return "phone should only contain number";
        }

    }

    public String checkStatus(String status) {
        if (status.isEmpty()) {
            return "status is empty";
        }
        try {
            int statupdate = Integer.parseInt(status);
            return "";
        } catch (NumberFormatException e) {
            System.out.println(e);
            return "the status is not a number 0 or 1, try again";
        }
    }

    public String checkForgotPassword_email(String email) {
        try {
            if (email.isEmpty()) {
                return "ERROR please enter your email to verify your account";
            }
            Pattern pattern1 = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}+\\.[a-zA-Z]{2,6}$");
            Matcher matcher1 = pattern1.matcher(email.trim());
            Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,6}$");
            Matcher matcher2 = pattern2.matcher(email.trim());
            if (matcher1.matches() || matcher2.matches()) {
                Account getAccount = accountRepository.findByEmailEquals(email);
                if (getAccount == null) {
                    return "ERROR cant find account for this email";
                } else {
                    return "";
                }
            } else {
                return "ERROR enter the correct email format";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "ERROR internal server, try again";
        }
    }

    public String CheckForgotPassword_sms(String phonenumber) {
        try {
            Pattern pattern = Pattern.compile("^\\d{10,12}$");
            Matcher matcher = pattern.matcher(phonenumber);
            if (matcher.matches() == false) {
                return "ERROR inappropriate phonenumber format";
            }
            List<Phone> phoneList = phoneRepository.findByNumber(phonenumber);
            if (phoneList.isEmpty() == false) {
                Phone getPhone = phoneList.get(0);
                Account getAccount = getPhone.getAccount();
                if (getAccount != null) {
                    return "";
                } else {
                    return "ERROR cant find account for this phone ";
                }
            } else {
                return "ERROR phone don't exist under our database, try contact manager ";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "ERROR internal server, try again";
        }
    }

}



