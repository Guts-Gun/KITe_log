package gutsandgun.kite_log.type;

public enum SendingStatus {
	/**
	 * 완료
	 */
	COMPLETE,
	/**
	 * 대기
	 */
	PENDING,
	/**
	 * 실패
	 */
	FAIL,
	/**
	 * 지연
	 */
	DELAY,
	/**
	 * 진행중
	 */
	SENDING,
}
