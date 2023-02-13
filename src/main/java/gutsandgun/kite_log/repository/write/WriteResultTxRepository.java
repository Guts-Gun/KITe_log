package gutsandgun.kite_log.repository.write;

import gutsandgun.kite_log.entity.write.ResultTx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WriteResultTxRepository extends JpaRepository<ResultTx, Long> {
    ResultTx findBySendingIdAndTxId(Long sendingId, Long TxId);
}
