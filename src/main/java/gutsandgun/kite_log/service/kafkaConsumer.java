package gutsandgun.kite_log.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class kafkaConsumer {
    private final Logging logging;
    @KafkaListener(topics="fluentd-container-logging",groupId="spring-log")
    public void consume(String message) throws IOException, InterruptedException {
        String msg = message.replace("\\","").replace("\"","");
        if(msg.contains("namespace_name:service") &&
                !msg.contains("container_name:log")){
            logging.LogSave(msg);
        }
    }
}
