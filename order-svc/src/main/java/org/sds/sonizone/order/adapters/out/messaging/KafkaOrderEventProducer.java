package org.sds.sonizone.order.adapters.out.messaging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaOrderEventProducer<T> {
    private static final Logger logger = LogManager.getLogger(KafkaOrderEventProducer.class);
    private final KafkaTemplate<String, T> kafkaTemplate;

    public KafkaOrderEventProducer(KafkaTemplate<String, T> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, T message) {
        kafkaTemplate.send(topic, message);
    }
}