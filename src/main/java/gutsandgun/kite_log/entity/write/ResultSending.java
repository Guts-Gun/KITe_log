package gutsandgun.kite_log.entity.write;

import gutsandgun.kite_log.entity.BaseTimeEntity;
import gutsandgun.kite_log.type.SendingRuleType;
import gutsandgun.kite_log.type.SendingStatus;
import gutsandgun.kite_log.type.SendingType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Sending 결과
 */
@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE result_sending SET is_deleted=true WHERE id = ?")
@Table(
		indexes = {
				@Index(name = "idx_result_sending_user_id", columnList = "fk_user_id"),
				@Index(name = "idx_result_sending_sending_id", columnList = "fk_sending_id")
		})
public class ResultSending extends BaseTimeEntity {

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
	 * 발송 메시지 타입
	 */
	@Comment("발송 메시지 타입")
	private SendingType sendingType;

	/**
	 * 분배 규칙 타입
	 */
	@Comment("분배 규칙 타입")
	private SendingRuleType sendingRuleType;

	/**
	 * 발송 상태
	 */

	@Comment("발송 상태")
	@ColumnDefault("1")
	private SendingStatus sendingStatus = SendingStatus.PENDING;

	/**
	 * 성공 여부
	 */
	@Comment("성공 여부")
	private Boolean success;

	/**
	 * 전체 메시지 개수
	 */
	@Comment("전체 메시지 개수")
	private Long totalMessage;

	/**
	 * 실패한 메시지 개수
	 */
	@Comment("실패한 메시지 개수")
	private Long failedMessage;

	/**
	 * 평균 응답속도
	 */
	@Comment("평군 응답속도")
	private Float avgLatency;


	// 시간 관련 로그
	/**
	 * 입력 시간 unix time
	 */
	@Comment("입력 시간")
	private Long inputTime;

	/**
	 * 예약 시간 unix time
	 */
	@Comment("예약 시간")
	private Long scheduleTime;

	/**
	 * 시작 시간 unix time
	 */
	@Comment("시작 시간")
	private Long startTime;

	/**
	 * 완료 시간 unix time
	 */
	@Comment("완료 시간")
	private Long completeTime;

	/**
	 * 결과 기록 시간
	 */
	@Comment("결과 기록 시간")
	private Long logTime;

	@ColumnDefault("false")
	private Boolean isDeleted = false;
}
