package gutsandgun.kite_log.entity.read;

import gutsandgun.kite_log.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * 발송할 e mail tx 테이블
 */
@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE sending_email SET is_deleted=true WHERE id = ?")
@Table(
		indexes = {
				@Index(name = "idx_sending_email_sending_id", columnList = "fk_sending_id")
		})
public class SendingEmail extends BaseTimeEntity {

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
	 * 발신자 주소
	 */
	@Comment("발신자 주소")
	private String sender;

	/**
	 * 수신자 주소
	 */
	@Comment("수신자 주소")
	private String receiver;

	/**
	 * 사용자지정 변수 - 이름
	 */
	@Comment("사용자지정 변수 - 이름")
	private String name;

	/**
	 * 사용자지정 변수 - 1번
	 */
	@Comment("사용자지정 변수 - 1번")
	private String var1;

	/**
	 * 사용자지정 변수 - 2번
	 */
	@Comment("사용자지정 변수 - 2번")
	private String var2;

	/**
	 * 사용자지정 변수 - 2번
	 */
	@Comment("사용자지정 변수 - 2번")
	private String var3;


	@ColumnDefault("false")
	private Boolean isDeleted = false;


	@Comment("생성자")
	@Column(name = "reg_id", nullable = false, length = 20)
	private String regId;

	@Comment("수정자")
	@Column(name = "mod_id", length = 20)
	private String modId;

}
