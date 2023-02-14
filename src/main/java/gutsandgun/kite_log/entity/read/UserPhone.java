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
 * 발신 전화번호 저장 테이블
 */
@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE user_phone SET is_deleted=true WHERE id = ?")
@Table(name = "user_phone",
		indexes = {
				@Index(name = "idx_user_phone_user_id", columnList = "fk_user_id")
		})
public class UserPhone extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "fk_user_id")
	@Comment("user id")
	private String userId;

	@Comment("발신 이름")
	private String name;
	@Comment("발신 전화번호")
	private String phone;

	@ColumnDefault("false")
	private Boolean isDeleted = false;

	@Comment("생성자")
	@Column(name = "reg_id", nullable = false, length = 20)
	private String regId;

	@Comment("수정자")
	@Column(name = "mod_id", length = 20)
	private String modId;
}
