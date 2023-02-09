package gutsandgun.kite_log.entity.type;

public enum FailReason {
	/**
	 * 중계사 문제 ex) 중계사 지연, 수신거부
	 */
	BROKER,
	/**
	 * 시스템 문제 ex) tx 입력 실패, 윤재 부채질 문제
	 */
	SYSTEM,
	/**
	 * 사용자 문제 ex)
	 */
	USER,

	//Service : sendMsg (broker랑 통신하는 곳)

	/**
	 * broker error : broker 이상
	 */
	BAD_REQUEST,
	/**
	 * broker error : 전화번호 없음
	 */
	INVALID_PHONE,

}
