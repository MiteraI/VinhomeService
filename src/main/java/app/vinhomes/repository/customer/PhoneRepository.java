package app.vinhomes.repository.customer;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.customer.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
    @Query(value = "select p.number from tbl_phone p where p.account_id = ?1", nativeQuery=true)
    List<String> getPhoneNumberById(long account_id);

    List<Phone> findByAccount(Account account);

    List<Phone> findByNumber(String number);
    @Query(value = "select * from tbl_phone p where p.account_id = ?1", nativeQuery=true)
    List<Phone> findByAccountId(long account_id);

}
