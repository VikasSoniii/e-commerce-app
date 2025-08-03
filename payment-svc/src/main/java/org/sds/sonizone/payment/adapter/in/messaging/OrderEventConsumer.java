package org.sds.sonizone.payment.adapter.in.messaging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.sds.sonizone.common.events.CreateOrderEvent;
import org.sds.sonizone.common.events.PaymentCompletedEvent;
import org.sds.sonizone.common.events.PaymentFailedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger logger = LogManager.getLogger(OrderEventConsumer.class);
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = "order-events", groupId = "payment-group")
    public void consume(CreateOrderEvent event) {
        logger.info("Received CreateOrderEvent: {}", event);

        // TODO: Process payment based on event
        try {
            // Simulate payment processing
            if (event.getQuantity() > 10) { // Simulate failure
                logger.warn("Simulating payment failure for orderId: {} due to high quantity: {}", event.getId(), event.getQuantity());
                throw new RuntimeException("Insufficient balance");
            }
            PaymentCompletedEvent completedEvent = new PaymentCompletedEvent();
            completedEvent.setOrderId(event.getId());
            completedEvent.setStatus("COMPLETED");
            kafkaTemplate.send("payment-completed-topic", completedEvent);
            logger.info("Sent PaymentCompletedEvent for orderId: {}", completedEvent.getOrderId());
        } catch (Exception e) {
            logger.error("Payment failed for orderId: {}. Reason: {}", event.getId(), e.getMessage());
            PaymentFailedEvent failedEvent = new PaymentFailedEvent();
            failedEvent.setOrderId(event.getId());
            failedEvent.setReason(e.getMessage());
            kafkaTemplate.send("payment-failed-topic", failedEvent);
            logger.info("Sent PaymentFailedEvent for orderId: {}", failedEvent.getOrderId());
        }
    }
}
