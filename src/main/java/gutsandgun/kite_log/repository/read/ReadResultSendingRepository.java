package gutsandgun.kite_log.repository.read;

import gutsandgun.kite_log.entity.read.ResultSending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadResultSendingRepository extends JpaRepository<ResultSending, Long> {
}
