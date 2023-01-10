package gutsandgun.kite_log.repository.write;

import gutsandgun.kite_log.entity.write.LogTotal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriteLogTotalRepository extends JpaRepository<LogTotal, Long> {
}
