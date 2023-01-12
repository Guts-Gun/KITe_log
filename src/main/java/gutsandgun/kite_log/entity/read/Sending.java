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
@SQLDelete(sql= "UPDATE sending SET is_deleted=true WHERE id = ?")
@Table(name="sending")
public class Sending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fk_user_id")
    private Long userId;

    private String ruleType;

    private String sendingType;

    private Long totalSending;

    private Long requestTime;

    private Long scheduleTime;
    private String title;
    private String media_link;
    private String content;

    private Boolean isDeleted = false;
}
