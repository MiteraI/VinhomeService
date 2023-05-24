package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNameAndPassword(String accountName, String password);

    List<Account> findByRoleEquals(int role);
    Account findByEmailEquals(String email);
    Account findByAccountName(String accountName);

    //@Query("select * from tbl_account a left join tbl_phone b on a.account_id = b.account_id")
    //List<Account> getAccountWithPhone();
}
