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
@SQLDelete(sql= "UPDATE sending_msg SET is_deleted=true WHERE id = ?")
@Table(name="sending_msg")
public class SendingMsg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fk_sending_id")
    private Long sendingId;

    private String sender;

    private String receiver;

    private String contents;

    private Boolean isDeleted = false;
}
