package gutsandgun.kite_log.repository.write;

import gutsandgun.kite_log.entity.write.LogFailure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriteLogFailureRepository extends JpaRepository<LogFailure, Long> {
}
