package app.vinhomes.security.esms;

import app.vinhomes.controller.PageController;
import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.repository.customer.AddressRepository;
import app.vinhomes.repository.customer.PhoneRepository;
import app.vinhomes.security.esms.otp_dto.EnumOTPStatus;
import app.vinhomes.security.esms.otp_dto.OTPResponseStatus;
import app.vinhomes.security.esms.otp_service.ESMSservice;
import app.vinhomes.security.esms.otp_service.OTPService;
import app.vinhomes.service.AccountService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Iterator;

@RestController
@RequestMapping(value = "/esms")
public class ESMSController {
    private String phoneParameterName= "phoneNumber";
    private String OTPParameterName = "oneTimePassword";
    private String MessageParameterName = "message";
    @Autowired
    private ESMSservice service;
    @Autowired
    private OTPService otpService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PageController pageController;
    @PostMapping(value = "/sendnormal")
    public ResponseEntity<?> sendESMS(HttpServletRequest request) throws IOException {
        System.out.println("in send normal sms");
        String phonenumber = request.getParameter(phoneParameterName);
        String message = request.getParameter(MessageParameterName);
        String result = service.sendGetJSON(phonenumber,message);
        return ResponseEntity.ok().body(result);
    }
    @PostMapping(value = "/validateOTP",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateOTP(@RequestBody JsonNode jsonNode, HttpServletRequest request) {
        try{
            System.out.println("in validate OTP");
            String username = jsonNode.get("accountname").asText().trim();
            Account getAccount = accountService.getAccountByUsername(username);
            if(getAccount == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NO Account found");
            }
            String phonenumber =  phoneRepository.findByAccountId(getAccount.getAccountId()).get(0).getNumber();
            System.out.println(phonenumber);
            String OTP =  jsonNode.get("OTP").asText().trim();//request.getParameter(OTPParameterName);
            System.out.println(OTP);
            if(phonenumber.length() != 10 && phonenumber.length() != 12){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number is not legit, your phone lenght is: "+ phonenumber.length()+", which is not legit ");
            }
            System.out.println("now validate OTP");
            String returnValidation =otpService.validateOTP(OTP,phonenumber);
            if(returnValidation.startsWith("ERROR:")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(returnValidation);
            }else{
                accountService.setEnableToAccount(getAccount);
                pageController.updateSessionAccount(request);
                return ResponseEntity.status(HttpStatus.OK).body(returnValidation);
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR: Bad request, not sure what happent");
        }

    }
}
