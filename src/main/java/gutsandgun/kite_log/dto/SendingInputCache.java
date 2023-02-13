package gutsandgun.kite_log.dto;

import gutsandgun.kite_log.type.SendingType;
import lombok.Data;

@Data
public class SendingInputCache {

    private String title;

    private String content;

    private String mediaLink;

    private String sender;

    private String UserId;

    private SendingType sendingType;

    private Long resultSendingId;

    private Long inputTime;

    private Long scheduleTime;
}
