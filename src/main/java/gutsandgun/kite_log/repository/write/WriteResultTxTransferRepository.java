package gutsandgun.kite_log.repository.write;

import gutsandgun.kite_log.entity.write.ResultTx;
import gutsandgun.kite_log.entity.write.ResultTxTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WriteResultTxTransferRepository extends JpaRepository<ResultTxTransfer, Long> {
    ResultTxTransfer findBySendingIdAndTxId(Long sendingId, Long TxId);
}
