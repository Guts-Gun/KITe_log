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
@SQLDelete(sql= "UPDATE sending_rule SET is_deleted=true WHERE id = ?")
@Table(name="sending_rule")
public class SendingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "fk_user_id")
    private Long userId;

    @Column(name = "fk_sending_id")
    private Long sendingId;

    @Column(name = "fk_broker_id")
    private Long brokerId;

    private Long weight;

    private Boolean isDeleted = false;
}
