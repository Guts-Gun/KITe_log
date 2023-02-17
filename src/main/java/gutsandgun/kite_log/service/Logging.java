package gutsandgun.kite_log.service;

import gutsandgun.kite_log.dto.SendingInputCache;
import gutsandgun.kite_log.entity.write.ResultSending;
import gutsandgun.kite_log.entity.write.ResultTx;
import gutsandgun.kite_log.entity.write.ResultTxFailure;
import gutsandgun.kite_log.entity.write.ResultTxTransfer;
import gutsandgun.kite_log.repository.write.WriteResultSendingRepository;
import gutsandgun.kite_log.repository.write.WriteResultTxFailureRepository;
import gutsandgun.kite_log.repository.write.WriteResultTxRepository;
import gutsandgun.kite_log.repository.write.WriteResultTxTransferRepository;
import gutsandgun.kite_log.type.FailReason;
import gutsandgun.kite_log.type.SendingRuleType;
import gutsandgun.kite_log.type.SendingStatus;
import gutsandgun.kite_log.type.SendingType;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class Logging {
    private final LogCache logCache;

    private final WriteResultSendingRepository writeResultSendingRepository;

    private final WriteResultTxFailureRepository writeResultTxFailureRepository;

    private final WriteResultTxRepository writeResultTxRepository;

    private final WriteResultTxTransferRepository writeResultTxTransferRepository;

    public void LogSave(String msg){
        String logging=msg;
        if(logging.contains("Service: request")){
            System.out.println(logging);
            logging=logging.substring(logging.indexOf("Service: request"));
            logging=logging.substring(logging.indexOf(",")+2);
            if(logging.contains("type: genSendingId")){
                ResultSending resultSending=new ResultSending();

                logging=logging.substring(logging.indexOf("type: genSendingId"));
                logging=logging.substring(logging.indexOf(",")+2);

                resultSending.setSendingId(Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(","))));
                logging=logging.substring(logging.indexOf(",")+2);

                resultSending.setSendingType(SendingType.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(","))));
                logging=logging.substring(logging.indexOf(",")+2);

                resultSending.setSendingRuleType(SendingRuleType.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(","))));
                logging=logging.substring(logging.indexOf(",")+2);

                resultSending.setTotalMessage(Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(","))));
                logging=logging.substring(logging.indexOf(",")+2);

                //resultSending.setReplace(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                //
                SendingInputCache sendingInputCache=new SendingInputCache();

                sendingInputCache.setTitle(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                sendingInputCache.setContent(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                sendingInputCache.setMediaLink(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                sendingInputCache.setSender(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                //

                resultSending.setUserId(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                resultSending.setLogTime(Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(","))));
                logging=logging.substring(logging.indexOf(",")+2);

                resultSending.setInputTime(Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(","))));
                logging=logging.substring(logging.indexOf(",")+2);

                resultSending.setScheduleTime(Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@"))));

                resultSending.setSendingStatus(SendingStatus.PENDING);

                writeResultSendingRepository.save(resultSending);

                sendingInputCache.setResultSendingId(resultSending.getId());

                sendingInputCache.setInputTime(resultSending.getInputTime());

                sendingInputCache.setScheduleTime(resultSending.getScheduleTime());

                sendingInputCache.setSendingType(resultSending.getSendingType());

                sendingInputCache.setUserId(resultSending.getUserId());

                logCache.SendingInputCache(resultSending.getSendingId(), sendingInputCache);
            }
            else if(logging.contains("type: input")){
                ResultTx resultTx=new ResultTx();

                logging=logging.substring(logging.indexOf("type: input"));
                logging=logging.substring(logging.indexOf(",")+2);

                Long sendingId=Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                resultTx.setTxId(Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(","))));
                logging=logging.substring(logging.indexOf(",")+2);

                resultTx.setSender(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                resultTx.setReceiver(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

                SendingInputCache sendingInputCache=logCache.SendingInputCache(sendingId,null);

                resultTx.setTitle(sendingInputCache.getTitle());

                resultTx.setContent(sendingInputCache.getContent());

                resultTx.setMediaLink(sendingInputCache.getMediaLink());

                resultTx.setUserId(sendingInputCache.getUserId());

                resultTx.setSendingType(sendingInputCache.getSendingType());

                resultTx.setResultSendingId(sendingInputCache.getResultSendingId());

                resultTx.setScheduleTime(sendingInputCache.getScheduleTime());

                resultTx.setInputTime(sendingInputCache.getInputTime());

                writeResultTxRepository.save(resultTx);
            }
        }
        else if(logging.contains("Service: sendingManager")){
            System.out.println(logging);
            logging=logging.substring(logging.indexOf("Service: sendingManager"));
            logging=logging.substring(logging.indexOf(",")+2);
            if(logging.contains("type: sendingStart")){
                logging=logging.substring(logging.indexOf("type: sendingStart"));
                logging=logging.substring(logging.indexOf(",")+2);

                Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                String sendingType=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
                logging=logging.substring(logging.indexOf(",")+2);

                Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

                ResultSending resultSending=writeResultSendingRepository.findBySendingId(sendingId);

                resultSending.setStartTime(time);

                writeResultSendingRepository.save(resultSending);
            }
            else if(logging.contains("type: pushQueue")){
                logging=logging.substring(logging.indexOf("type: pushQueue"));
                logging=logging.substring(logging.indexOf(",")+2);

                Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                String sendingType=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
                logging=logging.substring(logging.indexOf(",")+2);

                Long brokerId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long TxId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

                Long resultSendingId=writeResultSendingRepository.findBySendingId(sendingId).getId();

                ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);

                resultTx.setStartTime(time);

                resultTx.setBrokerId(brokerId);

                writeResultTxRepository.save(resultTx);
            }
            else if(logging.contains("type: blocking")){ //fail
                logging=logging.substring(logging.indexOf("type: blocking"));
                logging=logging.substring(logging.indexOf(",")+2);

                String success=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
                logging=logging.substring(logging.indexOf(",")+2);

                Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                SendingType sendingType= SendingType.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long TxId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

                Long resultSendingId=writeResultSendingRepository.findBySendingId(sendingId).getId();

                ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);
                ResultTxFailure resultTxFailure=new ResultTxFailure();

                resultTx.setSuccess(success.indexOf("true")==-1 ? true : false);

                resultTx.setFailReason(FailReason.USER);
                resultTxFailure.setFailReason(FailReason.USER);

                if(resultTx.getScheduleTime()!=null){
                    resultTx.setLogTime(time-resultTx.getScheduleTime());
                }
                else{
                    resultTx.setLogTime(time-resultTx.getInputTime());
                }

                resultTxFailure.setUserId(resultTx.getUserId());

                resultTxFailure.setTitle(resultTx.getTitle());

                resultTxFailure.setContent(resultTx.getContent());

                resultTxFailure.setMediaLink(resultTx.getMediaLink());

                resultTxFailure.setId(resultTx.getId());

                resultTxFailure.setResultSendingId(resultTx.getResultSendingId());

                resultTxFailure.setTxId(TxId);

                resultTxFailure.setSendingType(sendingType);

                writeResultTxRepository.save(resultTx);
                writeResultTxFailureRepository.save(resultTxFailure);
            }
        }
        else if(logging.contains("Service: Send")){
            System.out.println(logging);
            logging=logging.substring(logging.indexOf("Service: Send"));
            logging=logging.substring(logging.indexOf(",")+2);
            if(logging.contains("type: sendBroker")){
                logging=logging.substring(logging.indexOf("type: sendBroker"));
                logging=logging.substring(logging.indexOf(",")+2);

                Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                SendingType sendingType= SendingType.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long brokerId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long TxId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                String content=logging.substring(logging.indexOf(":")+2,logging.indexOf("@"));

                Long resultSendingId=writeResultSendingRepository.findBySendingId(sendingId).getId();

                ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);
                ResultTxTransfer resultTxTransfer=new ResultTxTransfer();

                resultTx.setContent(content);

                resultTx.setBrokerId(brokerId);

                resultTxTransfer.setResultTxId(resultTx.getId());

                resultTxTransfer.setBrokerId(brokerId);

                resultTxTransfer.setSendTime(time);

                resultTxTransfer.setReceiver(resultTx.getReceiver());

                resultTxTransfer.setSender(resultTx.getSender());

                resultTxTransfer.setSendingType(sendingType);

                writeResultTxRepository.save(resultTx);
                writeResultTxTransferRepository.save(resultTxTransfer);

            }
            else if(logging.contains("type: receiveBroker")){ //success/fail
                logging=logging.substring(logging.indexOf("type: receiveBroker"));
                logging=logging.substring(logging.indexOf(",")+2);

                String success=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
                logging=logging.substring(logging.indexOf(",")+2);

                FailReason failReason= FailReason.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                SendingType sendingType= SendingType.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long brokerId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long TxId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

                Long resultSendingId=writeResultSendingRepository.findBySendingId(sendingId).getId();

                ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);
                ResultTxTransfer resultTxTransfer=writeResultTxTransferRepository.findByBrokerIdAndResultTxId(brokerId, resultTx.getId());

                resultTx.setSuccess(success.indexOf("true")==-1 ? true : false);
                resultTxTransfer.setSuccess(success.indexOf("true")==-1 ? true : false);

                if(success.indexOf("true")==-1){
                    resultTx.setFailReason(failReason);
                    resultTxTransfer.setFailReason(failReason);
                }
                else{
                    ResultTxFailure resultTxFailure=new ResultTxFailure();

                    resultTxFailure.setUserId(resultTx.getUserId());

                    resultTxFailure.setTitle(resultTx.getTitle());

                    resultTxFailure.setContent(resultTx.getContent());

                    resultTxFailure.setMediaLink(resultTx.getMediaLink());

                    resultTxFailure.setId(resultTx.getId());

                    resultTxFailure.setResultSendingId(resultTx.getResultSendingId());

                    resultTxFailure.setTxId(TxId);

                    resultTxFailure.setSendingType(sendingType);

                    resultTxFailure.setBrokerId(brokerId);

                    writeResultTxFailureRepository.save(resultTxFailure);
                }

                resultTxTransfer.setCompleteTime(time);

                writeResultTxTransferRepository.save(resultTxTransfer);
                writeResultTxRepository.save(resultTx);
            }
            else if(logging.contains("type: missingSendingId")){ //fail
                logging=logging.substring(logging.indexOf("type: missingSendingId"));
                logging=logging.substring(logging.indexOf(",")+2);

                Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                FailReason failReason= FailReason.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long brokerId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long TxId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
                logging=logging.substring(logging.indexOf(",")+2);

                Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

                Long resultSendingId=writeResultSendingRepository.findBySendingId(sendingId).getId();

                ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);
                ResultTxFailure resultTxFailure=new ResultTxFailure();

                resultTx.setSuccess(false);

                resultTx.setFailReason(failReason);
                resultTxFailure.setFailReason(failReason);

                resultTxFailure.setUserId(resultTx.getUserId());

                resultTxFailure.setTitle(resultTx.getTitle());

                resultTxFailure.setContent(resultTx.getContent());

                resultTxFailure.setMediaLink(resultTx.getMediaLink());

                resultTxFailure.setId(resultTx.getId());

                resultTxFailure.setResultSendingId(resultTx.getResultSendingId());

                resultTxFailure.setTxId(TxId);

                resultTxFailure.setSendingType(resultTx.getSendingType());

                resultTxFailure.setBrokerId(brokerId);

                writeResultTxRepository.save(resultTx);
                writeResultTxFailureRepository.save(resultTxFailure);
            }
        }
    }
}
