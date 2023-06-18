package app.vinhomes.service;

import app.vinhomes.entity.order.Payment;

import app.vinhomes.repository.order.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
@Service
public class PaymentService
{
    @Autowired
    private PaymentRepository paymentRepository;
    public ResponseEntity<String> getPaymentCategory(String paymentId){
        try{
            long parsedPaymentId = Long.parseLong(paymentId);
            Payment getPayment = paymentRepository.findById(parsedPaymentId).get();
            return ResponseEntity.ok().body(getPayment.getPaymentName().trim());
        }catch (NumberFormatException e){
            System.out.println("error inside paymentService: "+ e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR : fail to parse payment ID");
        }catch (NoSuchElementException e){
            System.out.println("error inside paymentService: "+ e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR : fail to get payment");
        }
    }
}
