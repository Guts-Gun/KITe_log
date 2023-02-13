package gutsandgun.kite_log.service;

import gutsandgun.kite_log.dto.SendingInputCache;
import gutsandgun.kite_log.repository.read.ReadResultSendingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogCache {

    @Cacheable(value = "logSendingId", key = "#sendingId", cacheManager = "CacheManager")
    public SendingInputCache SendingInputCache(Long sendingId, SendingInputCache sendingInputCache){
        return sendingInputCache;
    }
}
