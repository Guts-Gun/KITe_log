package gutsandgun.kite_log.service;

import gutsandgun.kite_log.entity.write.ResultSending;
import gutsandgun.kite_log.entity.write.ResultTx;
import gutsandgun.kite_log.entity.write.ResultTxFailure;
import gutsandgun.kite_log.entity.write.ResultTxTransfer;
import gutsandgun.kite_log.repository.write.WriteResultSendingRepository;
import gutsandgun.kite_log.repository.write.WriteResultTxFailureRepository;
import gutsandgun.kite_log.repository.write.WriteResultTxRepository;
import gutsandgun.kite_log.repository.write.WriteResultTxTransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DBWrite {

    private final WriteResultSendingRepository writeResultSendingRepository;

    private final WriteResultTxFailureRepository writeResultTxFailureRepository;

    private final WriteResultTxRepository writeResultTxRepository;

    private final WriteResultTxTransferRepository writeResultTxTransferRepository;

    @Transactional
    public ResultTx writeResultTx(ResultTx dto){
        return writeResultTxRepository.save(dto);
    }
    @Transactional
    public ResultTxTransfer writeResultTxTransfer(ResultTxTransfer dto){
        return writeResultTxTransferRepository.save(dto);
    }
    @Transactional
    public ResultSending writeResultSending(ResultSending dto){
        return writeResultSendingRepository.save(dto);
    }
    @Transactional
    public ResultTxFailure writeResultTxFailure(ResultTxFailure dto){
        return writeResultTxFailureRepository.save(dto);
    }
}
