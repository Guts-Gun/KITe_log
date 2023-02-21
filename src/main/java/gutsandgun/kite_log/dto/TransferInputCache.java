package gutsandgun.kite_log.dto;

import gutsandgun.kite_log.type.SendingType;
import lombok.Data;

@Data
public class TransferInputCache {

    private Long resultTxId;

    private Long brokerId;

    private Long sendTime;

    private String sender;

    private String receiver;

    private SendingType sendingType;
}

