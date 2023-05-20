package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Address;
import app.vinhomes.entity.Phone;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

   // @Test
    public void addAccount() {
        Address address = Address.builder().buildingBlock("D6").buildingRoom("411").build();
        Phone phone1 = Phone.builder().number("0123456789").build();
        Phone phone2 = Phone.builder().number("0123456780").build();
        Account account = Account.builder().accountName("Kiet")
                .password("12345")
                .email("kiiaeet@gmail.com")
                .accountName("kiethased")
                .address(address)
                .build();
        phone1.setAccount(account);
        phone2.setAccount(account);
        account.addPhone(phone1);
        account.addPhone(phone2);
        accountRepository.save(account);
    }
   // @Test
    public void printAllAccount() {
        System.out.println("Accounts info = " + accountRepository.findAll());
    }
   // @Test
    public void deleteAccount() {
        accountRepository.deleteAll();
    }
}