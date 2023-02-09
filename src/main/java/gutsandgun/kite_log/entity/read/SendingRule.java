package gutsandgun.kite_log.entity.read;

import gutsandgun.kite_log.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE sending_rule SET is_deleted=true WHERE id = ?")
@Table(name = "sending_rule")
public class SendingRule extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	/**
	 * user id
	 */
	@Column(name = "fk_user_id")
	@Comment("user id")
	private String userId;

	/**
	 * sending id
	 */
	@Column(name = "fk_sending_id")
	@Comment("sending id")
	private Long sendingId;

	/**
	 * 중계사 id
	 */
	@Column(name = "fk_broker_id")
	@Comment("중계사 id")
	private Long brokerId;

	/**
	 * 발송 비중
	 */
	@Comment("발송 비중")
	private Long weight;

	@ColumnDefault("false")
	private Boolean isDeleted = false;
}
