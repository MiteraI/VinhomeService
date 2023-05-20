package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByAccountNameAndPassword(String accountName, String password);
}
