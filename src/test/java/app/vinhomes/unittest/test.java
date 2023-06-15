package app.vinhomes.unittest;

import app.vinhomes.entity.Transaction;
import app.vinhomes.repository.TransactionRepository;
import app.vinhomes.vnpay.service.ValidationAndBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
public class test {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ValidationAndBuilder validationAndBuilder;
//    @Value("${twilio.expiration}")
    private int expired = 120000;

    @Test
    @Disabled
    void printExpired(){
        assertThat(expired).isEqualTo(120000);
    }
    @Test
    @Disabled
    void testTransactionRepo(){
        List<Transaction> transactionList= transactionRepository.findAll();
        int size = transactionList.size();
    }
    @Test
    void testTransaction(){
        validationAndBuilder.BuildTransactionThroughOrder(
            37,
                "123123",
                "8"
        );
    }
}
