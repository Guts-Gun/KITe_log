package gutsandgun.kite_log.repository.write;

import gutsandgun.kite_log.entity.write.ResultTxFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WriteResultTxFailureRepository extends JpaRepository<ResultTxFailure, Long> {
}
