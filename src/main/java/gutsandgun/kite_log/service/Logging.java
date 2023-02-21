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

        if(writeResultSendingRepository.findBySendingId(resultSending.getSendingId())!=null){
            return;
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

        Boolean save=false;
        while(!save){
            ResultSending verification=null;
            try{
                verification=writeResultSendingRepository.saveAndFlush(resultSending);
                save=true;
                if(verification==null){
                    save=false;
                }
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
                //logCache.SendingDeleteCache(resultSending.getSendingId());
                sendingCache=logCache.SendingInputCache(resultSending.getSendingId(), sendingInputCache);
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
            return false;
        }

        if(writeResultTxRepository.findByResultSendingIdAndTxId(sendingInputCache.getResultSendingId(),resultTx.getTxId())!=null) {
            return true;
        }

        resultTx.setTitle(sendingInputCache.getTitle());

        resultTx.setContent(sendingInputCache.getContent());

        resultTx.setMediaLink(sendingInputCache.getMediaLink());

        resultTx.setUserId(sendingInputCache.getUserId());

        resultTx.setSendingType(sendingInputCache.getSendingType());

        resultTx.setResultSendingId(sendingInputCache.getResultSendingId());

        resultTx.setScheduleTime(sendingInputCache.getScheduleTime());

        resultTx.setInputTime(sendingInputCache.getInputTime());

        Boolean save=false;
        while(!save){
            ResultTx verification=null;
            try{
                verification=writeResultTxRepository.saveAndFlush(resultTx);
                save=true;
                if(verification==null){
                    save=false;
                }
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

        ResultSending resultSending=writeResultSendingRepository.findBySendingId(sendingId);

        if(resultSending==null){
            log.warn("type: sendingStart, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        resultSending.setStartTime(time);

        resultSending.setSendingStatus(SendingStatus.SENDING);

        Boolean save=false;
        while(!save){
            ResultSending verification=null;
            try{
                verification=writeResultSendingRepository.saveAndFlush(resultSending);
                save=true;
                if(verification.getStartTime()!=resultSending.getStartTime()){
                    save=false;
                }
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


        ResultSending resultSending=writeResultSendingRepository.findBySendingId(sendingId);

        if(resultSending==null){
            log.warn("type: pushQueue, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);

        if(resultTx==null){
            log.warn("type: pushQueue, ResultTx is null. send Queue, TxId: "+TxId);
            return false;
        }

        resultTx.setStartTime(time);

        resultTx.setBrokerId(brokerId);

        Boolean save=false;
        while(!save){
            ResultTx verification=null;
            try{
                verification=writeResultTxRepository.saveAndFlush(resultTx);
                save=true;
                if(verification.getStartTime()!=resultTx.getStartTime()){
                    save=false;
                }
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

        ResultSending resultSending=writeResultSendingRepository.findBySendingId(sendingId);

        if(resultSending==null){
            log.warn("type: blocking, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);

        if(resultTx==null){
            log.warn("type: blocking, ResultTx is null. send Queue, TxId: "+TxId);
            return false;
        }

        ResultTxFailure resultTxFailure=new ResultTxFailure();

        if(writeResultTxFailureRepository.findById(resultTx.getId()).isPresent()) {
            return true;
        }

        resultTx.setSuccess(success.indexOf("true")!=-1 ? true : false);

        resultTx.setFailReason(FailReason.USER);
        resultTxFailure.setFailReason(FailReason.USER);

        resultTxFailure.setUserId(resultTx.getUserId());

        resultTxFailure.setTitle(resultTx.getTitle());

        resultTxFailure.setContent(resultTx.getContent());

        resultTxFailure.setMediaLink(resultTx.getMediaLink());

        resultTxFailure.setId(resultTx.getId());

        resultTxFailure.setResultSendingId(resultTx.getResultSendingId());

        resultTxFailure.setTxId(TxId);

        resultTxFailure.setSendingType(sendingType);

        Boolean save=false;
        while(!save){
            ResultTx verification=null;
            try{
                verification=writeResultTxRepository.saveAndFlush(resultTx);
                save=true;
                if(verification.getFailReason()!=resultTx.getFailReason()){
                    save=false;
                }
            }catch(Exception e){}

        }

        save=false;
        while(!save){
            ResultTxFailure verification=null;
            try{
                verification=writeResultTxFailureRepository.saveAndFlush(resultTxFailure);
                save=true;
                if(verification==null){
                    save=false;
                }
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

        Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

        ResultSending resultSending=writeResultSendingRepository.findBySendingId(sendingId);

        if(resultSending==null){
            log.warn("type: sendBroker, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }


        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);

        if(resultTx==null){
            log.warn("type: sendBroker, ResultTx is null. send Queue, TxId: "+TxId);
            return false;
        }

        if(writeResultTxTransferRepository.findByBrokerIdAndResultTxId(brokerId,resultTx.getId())!=null) {
            return true;
        }

        TransferInputCache resultTxTransfer=new TransferInputCache();

        resultTxTransfer.setResultTxId(resultTx.getId());

        resultTxTransfer.setBrokerId(brokerId);

        resultTxTransfer.setSendTime(time);

        resultTxTransfer.setReceiver(resultTx.getReceiver());

        resultTxTransfer.setSender(resultTx.getSender());

        resultTxTransfer.setSendingType(sendingType);

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

        String success=logging.substring(logging.indexOf(":")+2,logging.indexOf(","));
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

        Long time= Long.valueOf(logging.substring(logging.indexOf(":")+2,logging.indexOf("@")));

        ResultSending resultSending=writeResultSendingRepository.findBySendingId(sendingId);

        if(resultSending==null){
            log.warn("type: receiveBroker, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);

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

        ResultTxTransfer resultTxTransfer=new ResultTxTransfer();

        resultTxTransfer.setResultTxId(transferCache.getResultTxId());

        resultTxTransfer.setBrokerId(transferCache.getBrokerId());

        resultTxTransfer.setSendTime(transferCache.getSendTime());

        resultTxTransfer.setReceiver(transferCache.getReceiver());

        resultTxTransfer.setSender(transferCache.getSender());

        resultTxTransfer.setSendingType(transferCache.getSendingType());

        resultTx.setBrokerId(brokerId);
        resultTx.setSuccess(success.indexOf("true")!=-1 ? true : false);
        resultTxTransfer.setSuccess(success.indexOf("true")!=-1 ? true : false);

        if(success.indexOf("true")!=-1){
            resultTx.setFailReason(failReason);
            resultTxTransfer.setFailReason(failReason);
        }
        else{
            ResultTxFailure resultTxFailure=new ResultTxFailure();

            if(writeResultTxFailureRepository.findById(resultTx.getId()).isPresent()) {
                return true;
            }

            resultTxFailure.setUserId(resultTx.getUserId());

            resultTxFailure.setTitle(resultTx.getTitle());

            resultTxFailure.setContent(resultTx.getContent());

            resultTxFailure.setMediaLink(resultTx.getMediaLink());

            resultTxFailure.setId(resultTx.getId());

            resultTxFailure.setResultSendingId(resultTx.getResultSendingId());

            resultTxFailure.setTxId(TxId);

            resultTxFailure.setSendingType(sendingType);

            resultTxFailure.setBrokerId(brokerId);

            Boolean save=false;
            while(!save){
                ResultTxFailure verification=null;
                try{
                    verification=writeResultTxFailureRepository.saveAndFlush(resultTxFailure);
                    save=true;
                    if(verification==null){
                        save=false;
                    }
                }catch(Exception e){}
            }
        }

        resultTxTransfer.setCompleteTime(time);

        Boolean save=false;
        while(!save){
            ResultTxTransfer verification=null;
            try{
                verification=writeResultTxTransferRepository.saveAndFlush(resultTxTransfer);
                save=true;
                if(verification.getCompleteTime()!=resultTxTransfer.getCompleteTime()){
                    save=false;
                }
            }catch(Exception e){}
        }

        log.info("type: receiveBroker, receiveBroker is updated, resultTxId: "+resultTx.getId()+", resultTxTransfer: "+resultTxTransfer.getId());

        save=false;
        while(!save){
            ResultTx verification=null;
            try{
                verification=writeResultTxRepository.saveAndFlush(resultTx);
                save=true;
                if(verification.getFailReason()!=resultTx.getFailReason()){
                    save=false;
                }
            }catch(Exception e){}
        }

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

        ResultSending resultSending=writeResultSendingRepository.findBySendingId(sendingId);

        if(resultSending==null){
            log.warn("type: missingSendingId, ResultSending is null. send Queue, sendingId: "+sendingId);
            return false;
        }

        Long resultSendingId=resultSending.getId();

        ResultTx resultTx=writeResultTxRepository.findByResultSendingIdAndTxId(resultSendingId, TxId);

        if(resultTx==null){
            log.warn("type: missingSendingId, ResultTx is null. send Queue, TxId: "+TxId);
            return false;
        }

        ResultTxFailure resultTxFailure=new ResultTxFailure();

        if(writeResultTxFailureRepository.findById(resultTx.getId()).isPresent()) {
            return true;
        }

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

        Boolean save=false;
        while(!save){
            ResultTx verification=null;
            try{
                verification=writeResultTxRepository.saveAndFlush(resultTx);
                save=true;
                if(verification.getFailReason()!=resultTx.getFailReason()){
                    save=false;
                }
            }catch(Exception e){}
        }

        save=false;
        while(!save){
            ResultTxFailure verification=null;
            try{
                verification=writeResultTxFailureRepository.saveAndFlush(resultTxFailure);
                save=true;
                if(verification==null){
                    save=false;
                }
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

        ResultSending resultSending=writeResultSendingRepository.findById(resultsendingId).get();


        if(resultSending==null){
            log.warn("type: complete, ResultSending is null. send Queue, resultSendingId: "+resultsendingId);
            return false;
        }

        resultSending.setSendingStatus(status);

        resultSending.setAvgLatency(avgLatency);

        resultSending.setFailedMessage(failedMessage);

        resultSending.setCompleteTime(completeTime);

        Boolean save=false;
        while(!save){
            ResultSending verification=null;
            try{
                verification=writeResultSendingRepository.saveAndFlush(resultSending);
                save=true;
                if(verification.getCompleteTime()!=resultSending.getCompleteTime()){
                    save=false;
                }
            }catch(Exception e){}
        }

        log.info("type: complete, complete is updated, resultSendingId: "+resultSending.getId());

        return true;
    }
}
