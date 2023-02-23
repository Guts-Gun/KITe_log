package gutsandgun.kite_log.service;

import gutsandgun.kite_log.dto.SendingInputCache;
import gutsandgun.kite_log.dto.TransferInputCache;
import gutsandgun.kite_log.entity.write.ResultTxTransfer;
import gutsandgun.kite_log.repository.read.ReadResultSendingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogCache {

    @Cacheable(value = "logSendingId", key = "#Id", cacheManager = "CacheManager", unless="#result == null")
    public SendingInputCache SendingInputCache(Long Id, SendingInputCache sendingInputCache){
        return sendingInputCache;
    }

    @CacheEvict(value = "logSendingId", key = "#Id", cacheManager = "CacheManager")
    public void SendingDeleteCache(Long sendingId){}

    @Cacheable(value = "backSendingId", key = "#Id", cacheManager = "CacheManager", unless="#result == null")
    public SendingInputCache SendingInputBackup(Long Id, SendingInputCache sendingInputCache){
        return sendingInputCache;
    }

    @CacheEvict(value = "backSendingId", key = "#Id", cacheManager = "CacheManager")
    public void SendingDeleteCacheBackup(Long sendingId){}

    @Cacheable(value = "transferId", key = "#Id", cacheManager = "CacheManager", unless="#result == null")
    public TransferInputCache TransferInputCache(String Id, TransferInputCache resultTxTransfer){
        return resultTxTransfer;
    }

    @CacheEvict(value = "transferId", key = "#Id", cacheManager = "CacheManager")
    public void TransferDeleteCache(String Id){}
}
