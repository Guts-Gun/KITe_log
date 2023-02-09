package gutsandgun.kite_log.repository.read;

import gutsandgun.kite_log.entity.read.ResultTx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadResultTxRepository extends JpaRepository<ResultTx, Long> {
}
