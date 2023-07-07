package app.vinhomes.repository.customer;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
    List<Phone> findByAccount_AccountId(Long accountId);


    List<Phone> findByAccount(Account account);

    List<Phone> findByNumber(String number);


    //Phone findByNumber(String number);
    @Query(value = "select * from tbl_phone p where p.account_id = ?1", nativeQuery=true)
    List<Phone> findByAccountId(long account_id);

}