package gutsandgun.kite_log.repository.write;

import gutsandgun.kite_log.entity.write.LogSending;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriteLogSendingRepository extends JpaRepository<LogSending, Long> {
}
