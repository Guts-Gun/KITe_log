package gutsandgun.kite_log.entity.read;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE log_failure SET is_deleted=true WHERE id = ?")
@Table(name="log_failure")
public class LogFailure {

    @Id
    @Column(name = "pk_log_total_id")
    private Long logTotalId;

    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "fk_log_sending_id")
    private Long logSendingId;

    @Column(name = "fk_sending_x_id")
    private Long sendingXId;

    private String sendingType;

    private String failureCause;
    private String title;
    private String media_link;
    private String content;

    private Boolean isDeleted = false;
}
