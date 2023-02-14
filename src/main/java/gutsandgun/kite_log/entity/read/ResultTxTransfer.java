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

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE result_tx_transfer SET is_deleted=true WHERE id = ?")
@Table(name = "result_tx_transfer",
		indexes = {
				@Index(name = "idx_result_tx_transfer_result_tx_id", columnList = "fk_result_tx_id")
		})
public class ResultTxTransfer extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	/**
	 * 메시지 RESULT TX의 id
	 */
	@Comment("메시지 result TX ID")
	@Column(name = "fk_result_tx_id")
	private Long resultTxId;

	/**
	 * 중계사 id
	 */
	@Column(name = "fk_broker_id")
	@Comment("중계사 id")
	private Long brokerId;

	/**
	 * 발송 메시지 타입
	 */
	@Comment("발송 메시지 타입")
	private SendingType sendingType;

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
	 * 성공 여부
	 */
	@Comment("성공 여부")
	private Boolean success;

	/**
	 * 실패 원인
	 */
	@Comment("실패 원인")
	private FailReason failReason;

	// 시간 관련 로그
	/**
	 * 중계사 전송 시간 unix time
	 */
	@Comment("중계사 전송 시간 시간")
	private Long sendTime;

	/**
	 * 중계사 응답 완료 시간 unix time
	 */
	@Comment("중계사 응답 완료 시간")
	private Long completeTime;

	/**
	 * 결과 기록 시간
	 */
	@Comment("결과 기록 시간")
	private Long logTime;

	@ColumnDefault("false")
	private Boolean isDeleted = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
