package app.vinhomes.repository;

import app.vinhomes.entity.Account;
import app.vinhomes.entity.Phone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Long> {

    List<Phone> findByAccount(Account account);
}
