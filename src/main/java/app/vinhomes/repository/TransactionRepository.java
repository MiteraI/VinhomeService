package app.vinhomes.repository;

import app.vinhomes.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    Transaction findByVnpTxnRef(String vnpTxnRef);
    Transaction findByVnpTxnRefAndTransactionId(String vnp_txtRef,long transactionId);
    Transaction findByTransactionId (Long id);
    List<Transaction> findAllByOrder_Account_AccountId(Long accountId);


}
