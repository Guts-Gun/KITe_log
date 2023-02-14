package gutsandgun.kite_log.entity.read;

import gutsandgun.kite_log.entity.BaseTimeEntity;
import gutsandgun.kite_log.type.FailReason;
import gutsandgun.kite_log.type.SendingType;
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
@SQLDelete(sql = "UPDATE result_tx_failure SET is_deleted=true WHERE id = ?")
@Table(
		indexes = {
				@Index(name = "idx_result_tx_failure_user_id", columnList = "fk_user_id"),
				@Index(name = "idx_result_tx_failure_result_sending_id", columnList = "fk_result_sending_id"),
				@Index(name = "idx_result_tx_failure_tx_id", columnList = "fk_tx_id")
		})
public class ResultTxFailure extends BaseTimeEntity {

	/**
	 * result tx id 1대 1 대응
	 */
	@Id
//    @Column(name = "pk_result_tx_id") comment 달면 굳이 이렇게 안해도 될듯?
	@Comment("result_tx_id")
	private Long id;

	/**
	 * user id
	 */
	@Comment("user id")
	@Column(name = "fk_user_id")
	private String userId;

	/**
	 * sending 결과의 id
	 */
	@Comment("sending 결과의 id")
	@Column(name = "fk_result_sending_id")
	private Long resultSendingId;

	/**
	 * 메시지 TX의 id
	 */
	@Comment("메시지 TX ID")
	@Column(name = "fk_tx_id")
	private Long txId;


	/**
	 * 중계사 id
	 */
	@Comment("중계사 id")
	@Column(name = "fk_broker_id")
	private Long brokerId;


	/**
	 * 발송 메시지 타입
	 */
	@Comment("발송 메시지 타입")
	private SendingType sendingType;

	/**
	 * 실패 원인
	 */
	@Comment("실패 원인")
	private FailReason failReason;

	/**
	 * 메시지 제목
	 */
	@Comment("제목")
	private String title;

	/**
	 * 미디어 호스팅 주소
	 */
	@Comment("미디어 호스팅 주소")
	private String mediaLink;

	/**
	 * 메시지 내용
	 */
	@Comment("메시지 내용")
	private String content;

	@ColumnDefault("false")
	private Boolean isDeleted = false;
}
