package gutsandgun.kite_log.entity.write;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE log_sending SET is_deleted=true WHERE id = ?")
@Table(name="log_sending")
public class LogSending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "fk_sending_id")
    private Long sendingId;

    private Long logTime;

    private String sendingType;

    private String ruleType;

    private String success;

    private Long totalSending;

    private Long failureSending;

    private Float avgSpeed;

    private Long requestTime;

    private Long completeTime;

    private Long scheduleTime;

    private Boolean isDeleted = false;
}
