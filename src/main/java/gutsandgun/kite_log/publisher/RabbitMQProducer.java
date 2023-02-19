package gutsandgun.kite_log.publisher;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
public class RabbitMQProducer {

    @Value("${rabbitmq.log.exchange}")
    private String logExchange;

    @Value("${rabbitmq.routing.key.log}")
    private String logRoutingKey;

    private RabbitTemplate rabbitTemplate;

    public RabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void logSendQueue(String msg){
        rabbitTemplate.convertAndSend(logExchange, logRoutingKey, msg);
    }
}
