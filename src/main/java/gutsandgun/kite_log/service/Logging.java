package gutsandgun.kite_log.service;

import gutsandgun.kite_log.dto.SendingInputCache;
import gutsandgun.kite_log.dto.TransferInputCache;
import gutsandgun.kite_log.entity.write.ResultSending;
import gutsandgun.kite_log.entity.write.ResultTx;
import gutsandgun.kite_log.entity.write.ResultTxFailure;
import gutsandgun.kite_log.entity.write.ResultTxTransfer;
import gutsandgun.kite_log.publisher.RabbitMQProducer;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.Result;
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

    private final RabbitMQProducer rabbitMQProducer;

    private final DBWrite dbWrite;

    public void LogSave(String msg) {
        String logging=msg;
        Boolean clear=true;
        if(logging.contains("Service: request")){
            log.info(logging);
            logging=logging.substring(logging.indexOf("Service: request"));
            logging=logging.substring(logging.indexOf(",")+2);
            if(logging.contains("type: genSendingId")){
                genSendingId(logging);
            }
            else if(logging.contains("type: input")){
                clear=input(logging);
            }
        }
        else if(logging.contains("Service: sendingManager")){
            log.info(logging);
            logging=logging.substring(logging.indexOf("Service: sendingManager"));
            logging=logging.substring(logging.indexOf(",")+2);
            if(logging.contains("type: sendingStart")){
                clear=sendingStart(logging);
            }
            else if(logging.contains("type: pushQueue")){
                clear=pushQueue(logging);
            }
            else if(logging.contains("type: blocking")){ //fail
                clear=blocking(logging);
            }
        }
        else if(logging.contains("Service: Send")){
            log.info(logging);
            logging=logging.substring(logging.indexOf("Service: Send"));
            logging=logging.substring(logging.indexOf(",")+2);
            if(logging.contains("type: sendBroker")){
                clear=sendBroker(logging);
            }
            else if(logging.contains("type: receiveBroker")){ //success/fail
                clear=receiveBroker(logging);
            }
            else if(logging.contains("type: missingSendingId")){ //fail
                clear=missingSendingId(logging);
            }
        }
        else if(logging.contains("Service: Result")){
            log.info(logging);
            logging=logging.substring(logging.indexOf("Service: Result"));
            logging=logging.substring(logging.indexOf(",")+2);
            if(logging.contains("type:")){
                clear=complete(logging);
            }
        }
        if(!clear){
            rabbitMQProducer.logSendQueue(msg);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void genSendingId(String logging) {
        ResultSending resultSending=new ResultSending();

        logging=logging.substring(logging.indexOf("type: genSendingId"));
        logging=logging.substring(logging.indexOf(",")+2);

        Long sendingId=Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);


        while(true){
            try{
                if(writeResultSendingRepository.findBySendingId(resultSending.getSendingId())!=null){
                    return;
                }
                break;
            }
            catch(Exception e){}
        }


        resultSending.setSendingId(sendingId);

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

        resultSending.setInputTime(Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(","))));
        logging=logging.substring(logging.indexOf(",")+2);

        try{
            resultSending.setScheduleTime(Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@"))));
        }
        catch(Exception e){
            //log.warn("ScheduleTime is "+logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));
        }

        resultSending.setSendingStatus(SendingStatus.PENDING);

        while(true){
            try{
                writeResultSendingRepository.save(resultSending);
                break;
            }catch(Exception e){}
        }

        log.info("type: genSendingId, genSendingId is written, resultSendingId: "+resultSending.getId());

        sendingInputCache.setResultSendingId(resultSending.getId());

        sendingInputCache.setInputTime(resultSending.getInputTime());

        sendingInputCache.setScheduleTime(resultSending.getScheduleTime());

        sendingInputCache.setSendingType(resultSending.getSendingType());

        sendingInputCache.setUserId(resultSending.getUserId());

        SendingInputCache sendingCache=logCache.SendingInputCache(resultSending.getSendingId(), sendingInputCache);

        if(sendingCache==null){
            log.warn("type: genSendingId, SendingInputCache is null. generating..., sendingId: "+resultSending.getSendingId());
            while(sendingCache==null){
                logCache.SendingDeleteCache(resultSending.getSendingId());
                sendingCache=logCache.SendingInputCache(resultSending.getSendingId(), sendingInputCache);
            }
            log.warn("type: getSendingId, SendingInputCache null is fixed, sendingId: "+resultSending.getSendingId());
        }

        sendingCache=logCache.SendingInputBackup(resultSending.getSendingId(), sendingInputCache);

        if(sendingCache==null){
            log.warn("type: genSendingId, SendingInputCache is null. generating..., sendingId: "+resultSending.getSendingId());
            while(sendingCache==null){
                logCache.SendingDeleteCacheBackup(resultSending.getSendingId());
                sendingCache=logCache.SendingInputBackup(resultSending.getSendingId(), sendingInputCache);
            }
            log.warn("type: getSendingId, SendingInputCache null is fixed, sendingId: "+resultSending.getSendingId());
        }


    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean input(String logging) {
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

        if(sendingInputCache==null){
            log.warn("type: input, SendingInputCache is null. send Queue, sendingId: "+sendingId);
            sendingInputCache=logCache.SendingInputBackup(sendingId,null);
            if(sendingInputCache==null){
                log.warn("type: input, SendingInputBackup is null. send Queue, sendingId: "+sendingId);
                return false;
            }

        }

        while(true){
            try{
                if(writeResultTxRepository.findByResultSendingIdAndTxId(sendingInputCache.getResultSendingId(),resultTx.getTxId())!=null) {
                    return true;
                }
                break;
            }
            catch(Exception e){}
        }

        resultTx.setTitle(sendingInputCache.getTitle());

        resultTx.setContent(sendingInputCache.getContent());

        resultTx.setMediaLink(sendingInputCache.getMediaLink());

        resultTx.setUserId(sendingInputCache.getUserId());

        resultTx.setSendingType(sendingInputCache.getSendingType());

        resultTx.setResultSendingId(sendingInputCache.getResultSendingId());

        resultTx.setScheduleTime(sendingInputCache.getScheduleTime());

        resultTx.setInputTime(sendingInputCache.getInputTime());

        while(true){
            try{
                writeResultTxRepository.save(resultTx);
                break;
            }catch(Exception e){}
        }

        log.info("type: input, input is written, resultTx: "+resultTx.getId());

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean sendingStart(String logging) {
        logging=logging.substring(logging.indexOf("type: sendingStart"));
        logging=logging.substring(logging.indexOf(",")+2);

        Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        String sendingType=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
        logging=logging.substring(logging.indexOf(",")+2);

        Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

        ResultSending resultSending = null;

        while(true){
            try{
                resultSending=writeResultSendingRepository.findBySendingId(sendingId);
                break;
            }catch(Exception e){}
        }

        if(resultSending==null){
            log.warn("type: sendingStart, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        resultSending.setStartTime(time);

        resultSending.setSendingStatus(SendingStatus.SENDING);

        while(true){
            try{
                writeResultSendingRepository.save(resultSending);
                break;
            }catch(Exception e){}

        }

        log.info("type: sendingStart, sendingStart is updated, resultSendingId: "+resultSending.getId());

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean pushQueue(String logging) {
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


        ResultSending resultSending=null;

        while(true){
            try{
                resultSending=writeResultSendingRepository.findBySendingId(sendingId);
                break;
            }catch(Exception e){}
        }

        if(resultSending==null){
            log.warn("type: pushQueue, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=null;

        while(true){
            try{
                resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);
                break;
            }catch(Exception e){}
        }

        if(resultTx==null){
            log.warn("type: pushQueue, ResultTx is null. send Queue, TxId: "+TxId);
            return false;
        }

        resultTx.setStartTime(time);

        resultTx.setBrokerId(brokerId);

        resultTx.setStatus(SendingStatus.SENDING);

        while(true){
            try{
                writeResultTxRepository.save(resultTx);
                break;
            }catch(Exception e){}

        }

        log.info("type: pushQueue, pushQueue is updated, resultTxId: "+resultTx.getId());

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean blocking(String logging) {
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

        ResultSending resultSending=null;

        while(true){
            try{
                resultSending=writeResultSendingRepository.findBySendingId(sendingId);
                break;
            }catch(Exception e){}
        }

        if(resultSending==null){
            log.warn("type: blocking, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=null;

        while(true){
            try{
                resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);
                break;
            }catch(Exception e){}
        }



        if(resultTx==null){
            log.warn("type: blocking, ResultTx is null. send Queue, TxId: "+TxId);
            return false;
        }

        ResultTxFailure resultTxFailure=new ResultTxFailure();

        if(resultTx.getStatus()==null){
            resultTx.setFailReason(FailReason.USER);
            resultTx.setStatus(SendingStatus.FAIL);
            while(true){
                try{
                    writeResultTxRepository.save(resultTx);
                    break;
                }catch(Exception e){}
            }
        }

        ResultTxTransfer resultTxTransfer=new ResultTxTransfer();

        resultTxFailure.setFailReason(FailReason.USER);
        resultTxTransfer.setFailReason(FailReason.USER);

        resultTxFailure.setUserId(resultTx.getUserId());

        resultTxFailure.setTitle(resultTx.getTitle());


        resultTxFailure.setContent(resultTx.getContent());

        resultTxFailure.setMediaLink(resultTx.getMediaLink());

        resultTxFailure.setId(resultTx.getId());

        resultTxFailure.setResultSendingId(resultTx.getResultSendingId());

        resultTxFailure.setTxId(TxId);

        resultTxFailure.setSendingType(sendingType);

        resultTxTransfer.setSuccess(false);

        resultTxTransfer.setSendTime(time);

        resultTxTransfer.setCompleteTime(time);

        resultTxTransfer.setResultTxId(resultTx.getId());

        resultTxTransfer.setSender(resultTx.getSender());

        resultTxTransfer.setReceiver(resultTx.getReceiver());

        resultTxTransfer.setSendingType(resultTx.getSendingType());

        while(true){
            try{
                writeResultTxFailureRepository.save(resultTxFailure);
                break;
            }catch(Exception e){}
        }

        while(true){
            try{
                writeResultTxTransferRepository.save(resultTxTransfer);
                break;
            }catch(Exception e){}
        }

        log.info("type: blocking, blocking is updated, resultTxId: "+resultTx.getId()+", resultTxFailure: "+resultTxFailure.getId());

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean sendBroker(String logging) {
        logging=logging.substring(logging.indexOf("type: sendBroker"));
        logging=logging.substring(logging.indexOf(",")+2);

        Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        SendingType sendingType=SendingType.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long brokerId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long TxId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        String sender=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
        logging=logging.substring(logging.indexOf(",")+2);

        String receiver=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
        logging=logging.substring(logging.indexOf(",")+2);

        String content=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
        logging=logging.substring(logging.indexOf(",")+2);

        Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

        ResultSending resultSending=null;

        while(true){
            try{
                resultSending=writeResultSendingRepository.findBySendingId(sendingId);
                break;
            }catch(Exception e){}
        }

        if(resultSending==null){
            log.warn("type: sendBroker, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }


        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=null;

        while(true){
            try{
                resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);
                break;
            }catch(Exception e){}
        }

        if(resultTx==null){
            log.warn("type: sendBroker, ResultTx is null. send Queue, TxId: "+TxId+", resultSendingId: "+resultSendingId);
            return false;
        }

        if(resultTx.getStatus()==SendingStatus.PENDING){
            log.warn("type: sendBroker, ResultTx is not sending. send Queue, TxId: "+TxId+", resultSendingId: "+resultSendingId);
            return false;
        }

        TransferInputCache resultTxTransfer=new TransferInputCache();

        resultTxTransfer.setResultTxId(resultTx.getId());

        resultTxTransfer.setBrokerId(brokerId);

        resultTxTransfer.setSendTime(time);

        resultTxTransfer.setReceiver(receiver);

        resultTxTransfer.setSender(sender);

        resultTxTransfer.setSendingType(sendingType);

        resultTxTransfer.setContent(content);

        String cacheKey=String.valueOf(brokerId).concat("+").concat(String.valueOf(resultTx.getId()));

        TransferInputCache transferCache=logCache.TransferInputCache(cacheKey, resultTxTransfer);

        if(transferCache==null){
            log.warn("type: sendBroker, TransferInputCache is null. generating..., brokerId: "+brokerId+", resultTxId: "+resultTx.getId());
            while(transferCache==null){
                //logCache.TransferDeleteCache(cacheKey);
                transferCache=logCache.TransferInputCache(cacheKey, resultTxTransfer);
            }
            log.warn("type: sendBroker, TransferInputCache null is fixed, brokerId: "+brokerId+", resultTxId: "+resultTx.getId());
        }

        log.info("type: sendBroker, sendBroker is written, resultTxId: "+resultTx.getId());

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean receiveBroker(String logging) {
        logging=logging.substring(logging.indexOf("type: receiveBroker"));
        logging=logging.substring(logging.indexOf(",")+2);

        SendingStatus success= SendingStatus.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        FailReason failReason=null;
        try{
            failReason=FailReason.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        }
        catch(Exception e){
            //log.warn("FailReason is "+logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        }
        logging=logging.substring(logging.indexOf(",")+2);

        Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        SendingType sendingType= SendingType.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long brokerId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long TxId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Boolean last= Boolean.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

        ResultSending resultSending=null;

        while(true){
            try{
                resultSending=writeResultSendingRepository.findBySendingId(sendingId);
                break;
            }catch(Exception e){}
        }

        if(resultSending==null){
            log.warn("type: receiveBroker, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=null;

        while(true){
            try{
                resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);
                break;
            }catch(Exception e){}
        }

        if(resultTx==null){
            log.warn("type: receiveBroker, ResultTx is null. send Queue, TxId: "+TxId);
            return false;
        }

        String cacheKey=String.valueOf(brokerId).concat("+").concat(String.valueOf(resultTx.getId()));

        TransferInputCache transferCache=logCache.TransferInputCache(cacheKey,null);

        if(transferCache==null){
            log.warn("type: receiveBroker, ResultTxTransfer is null. send Queue, resultTxId: "+resultTx.getId());
            return false;
        }

        log.info("type: receiveBroker, last is "+last+", resultTx: "+resultTx.getId()+", brokerId: "+brokerId);
        if(last){
            log.info("type: receiveBroker, last is "+last+", resultTx: "+resultTx.getId()+", brokerId: "+brokerId);
            resultTx.setReceiver(transferCache.getReceiver());
            resultTx.setSender(transferCache.getSender());
            resultTx.setContent(transferCache.getContent());
            resultTx.setSendingType(transferCache.getSendingType());
            resultTx.setBrokerId(brokerId);
            resultTx.setStatus(success);
            resultTx.setFailReason(failReason);
            while(true){
                try{
                    writeResultTxRepository.save(resultTx);
                    break;
                }catch(Exception e){}
            }
        }

        ResultTxTransfer resultTxTransfer=new ResultTxTransfer();

        resultTxTransfer.setResultTxId(transferCache.getResultTxId());

        resultTxTransfer.setBrokerId(transferCache.getBrokerId());

        resultTxTransfer.setSendTime(transferCache.getSendTime());

        resultTxTransfer.setReceiver(transferCache.getReceiver());

        resultTxTransfer.setSender(transferCache.getSender());

        resultTxTransfer.setSendingType(transferCache.getSendingType());

        resultTxTransfer.setSuccess(success==SendingStatus.COMPLETE);
        resultTxTransfer.setFailReason(failReason);

        if(success!=SendingStatus.COMPLETE){
            ResultTxFailure resultTxFailure=new ResultTxFailure();

            resultTxFailure.setFailReason(failReason);

            resultTxFailure.setUserId(resultTx.getUserId());

            resultTxFailure.setTitle(resultTx.getTitle());

            resultTxFailure.setContent(resultTx.getContent());

            resultTxFailure.setMediaLink(resultTx.getMediaLink());

            resultTxFailure.setId(resultTx.getId());

            resultTxFailure.setResultSendingId(resultTx.getResultSendingId());

            resultTxFailure.setTxId(TxId);

            resultTxFailure.setSendingType(sendingType);

            resultTxFailure.setBrokerId(brokerId);

            while(true){
                try{
                    writeResultTxFailureRepository.save(resultTxFailure);
                    break;
                }catch(Exception e){}
            }
        }

        resultTxTransfer.setCompleteTime(time);

        while(true){
            try{
                writeResultTxTransferRepository.save(resultTxTransfer);
                break;
            }catch(Exception e){}
        }

        log.info("type: receiveBroker, receiveBroker is updated, resultTxId: "+resultTx.getId()+", resultTxTransfer: "+resultTxTransfer.getId());

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean missingSendingId(String logging) {
        logging=logging.substring(logging.indexOf("type: missingSendingId"));
        logging=logging.substring(logging.indexOf(",")+2);

        Long sendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        FailReason failReason= FailReason.SYSTEM;
        //logging=logging.substring(logging.indexOf(",")+2);

        Long brokerId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long TxId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

        ResultSending resultSending=null;

        while(true){
            try{
                resultSending=writeResultSendingRepository.findBySendingId(sendingId);
                break;
            }catch(Exception e){}
        }

        if(resultSending==null){
            log.warn("type: missingSendingId, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=null;

        while(true){
            try{
                resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);
                break;
            }catch(Exception e){}
        }

        if(resultTx==null){
            log.warn("type: missingSendingId, ResultTx is null. send Queue, TxId: "+TxId);
            return false;
        }

        ResultTxFailure resultTxFailure=new ResultTxFailure();

        resultTx.setStatus(SendingStatus.FAIL);

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

        while(true){
            try{
                writeResultTxRepository.save(resultTx);
                break;
            }catch(Exception e){}
        }

        while(true){
            try{
                writeResultTxFailureRepository.save(resultTxFailure);
                break;
            }catch(Exception e){}
        }

        log.info("type: missingSendingId, missingSendingId is updated, resultTxId: "+resultTx.getId()+", resultTxFailure: "+resultTxFailure.getId());

        return true;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean complete(String logging) {
        logging=logging.substring(logging.indexOf("type:"));

        SendingStatus status= SendingStatus.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long resultsendingId= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        String success=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
        logging=logging.substring(logging.indexOf(",")+2);

        Long failedMessage= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Float avgLatency= Float.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long completeTime= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf(",")));
        logging=logging.substring(logging.indexOf(",")+2);

        Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

        ResultSending resultSending=null;

        while(true){
            try{
                resultSending=writeResultSendingRepository.findById(resultsendingId).get();
                break;
            }catch(Exception e){}
        }

        if(resultSending==null){
            log.warn("type: complete, ResultSending is null. send Queue, resultSendingId: "+resultsendingId);
            return false;
        }
        if(resultSending.getSendingStatus()==SendingStatus.PENDING){
            log.warn("type: complete, ResultSending is not sending. send Queue, resultSendingId: "+resultsendingId);
            return false;
        }

        resultSending.setSendingStatus(status);

        resultSending.setAvgLatency(avgLatency);

        resultSending.setFailedMessage(failedMessage);

        resultSending.setCompleteTime(completeTime);

        while(true){
            try{
                writeResultSendingRepository.save(resultSending);
                break;
            }catch(Exception e){}
        }

        log.info("type: complete, complete is updated, resultSendingId: "+resultSending.getId());

        return true;
    }
}
