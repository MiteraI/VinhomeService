package app.vinhomes.repository;

import app.vinhomes.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
//    Transaction findByVnp_Txn_Ref(String vnp_txnRef);
    Transaction findByVnpTxnRef(String vnp_txtRef);
    Transaction findByVnpTxnRefAndTransactionId(String vnp_txtRef,long transactionId);
}
