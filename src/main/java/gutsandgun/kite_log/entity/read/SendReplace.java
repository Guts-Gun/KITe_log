package gutsandgun.kite_log.entity.read;

import gutsandgun.kite_log.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@Where(clause = "is_deleted = false")
//@SQLDelete(sql= "UPDATE sending_msg SET is_deleted=true WHERE id = ?")
@Table(name = "send_replace")
public class SendReplace extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fk_tx_id")
	private Long id;

	private String receiver;

	private String sender;

	@Comment("생성자")
	@Column(name = "reg_id", nullable = false, length = 20)
	private String regId;

	@Comment("수정자")
	@Column(name = "mod_id", length = 20)
	private String modId;

}
