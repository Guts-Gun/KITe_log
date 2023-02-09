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
@SQLDelete(sql = "UPDATE sending_schedule SET is_deleted=true WHERE id = ?")
@Table(name = "sending_schedule")
public class SendingSchedule extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * sending id
	 */
	@Column(name = "fk_sending_id")
	@Comment("sending id")
	private Long sendingId;

	/**
	 * 예약 시간 unix time
	 */
	@Comment("예약 시간")
	private Long time;

	@ColumnDefault("false")
	private Boolean isDeleted = false;
}
