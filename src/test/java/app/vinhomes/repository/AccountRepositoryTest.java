package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Address;
import app.vinhomes.entity.customer.Phone;
import app.vinhomes.entity.worker.WorkerStatus;
import app.vinhomes.repository.order.ServiceCategoryRepository;
import app.vinhomes.repository.worker.WorkerStatusRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;

    @Autowired
    private WorkerStatusRepository workerStatusRepository;

    @Test
    public void addAccount() {
        Address address = Address.builder().buildingBlock("D6").buildingRoom("411").build();
        Phone phone1 = Phone.builder().number("0123456789").build();
        Phone phone2 = Phone.builder().number("0123456780").build();
        Account account = Account.builder()
                .password("12345")
                .email("kiiaet@gmail.com")
                .accountName("kiethase")
                .address(address)
                .build();
        phone1.setAccount(account);
        phone2.setAccount(account);
        account.addPhone(phone1);
        account.addPhone(phone2);
        accountRepository.save(account);
    }
    @Test
    public void addWorker() {
        for (int i = 0; i < 3; i++) {
            Address address = Address.builder().buildingBlock("0").buildingRoom("0").build();
            Account account = Account.builder()
                    .accountName("C"+Integer.toString(i))
                    .password("12345")
                    .email("nvc"+Integer.toString(i)+"@gmail.com")
                    .firstName("Nguyen Van")
                    .lastName("C"+Integer.toString(i))
                    .address(address)
                    .role(1)
                    .build();
            accountRepository.save(account);
            WorkerStatus workerStatus = WorkerStatus.builder()
                    .allowedDayOff(10)
                    .serviceCategory(serviceCategoryRepository.findById(2L).get())
                    .status(0)
                    .workCount(0)
                    .account(account)
                    .build();
            workerStatusRepository.save(workerStatus);
        }
    }
    @Test
    public void printAllAccount() {
        System.out.println("Accounts info = " + accountRepository.findAll());
    }

    @Test
    public void printAccountFromLogin() {
        System.out.println("Account info = " + accountRepository.findByAccountNameAndPassword("kiethased","12345"));
    }
    @Test
    public void deleteAccount() {
        accountRepository.deleteAll();
    }

}