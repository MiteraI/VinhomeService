package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNameAndPassword(String accountName, String password);
    List<Account> findByRole(int role);

    List<Account> findByRoleEquals(int role);
    Account findByEmailEquals(String email);
    Account findByAccountName(String accountName);

    Account findByFirstNameAndLastName (String firstName, String lastName);

    @Query("""
select a from Account a   
    where a.accountName = :account_name   
""")
    Optional<Account> findUsername(String account_name);

    Account findByAccountId (Long id);

}
