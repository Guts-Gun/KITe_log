package gutsandgun.kite_log.repository.read;

import gutsandgun.kite_log.entity.read.UserSendingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadUserSendingRuleRepository extends JpaRepository<UserSendingRule, Long> {
}
