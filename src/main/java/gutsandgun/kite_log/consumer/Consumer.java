package gutsandgun.kite_log.consumer;

import gutsandgun.kite_log.service.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class Consumer {
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);
    @Autowired
    private Logging logging;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    @RabbitListener(queues = "${rabbitmq.routing.key.log}")
    public void consumeLog(String msg) {
        executorService.submit(() -> logging.LogSave(msg));
        //logging.LogSave(msg);
    }
}
