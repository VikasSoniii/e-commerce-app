package org.sds.sonizone.order.adapters.out.persistence;

import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sds.sonizone.common.events.CreateOrderEvent;
import org.sds.sonizone.common.events.PaymentCompletedEvent;
import org.sds.sonizone.common.events.PaymentFailedEvent;
import org.sds.sonizone.common.utils.HashUtil;
import org.sds.sonizone.order.adapters.out.messaging.KafkaOrderEventProducer;

import org.sds.sonizone.order.config.OrderMapper;
import org.sds.sonizone.order.domain.model.Order;
import org.sds.sonizone.order.domain.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaOrderRepository implements OrderRepository {
    private static final Logger logger = LogManager.getLogger(JpaOrderRepository.class);

    private final KafkaOrderEventProducer kafkaProducer;

    @Value("${order.topic}")
    private String topic;

    private final SpringDataOrderRepository springDataOrderRepository;

    public JpaOrderRepository(SpringDataOrderRepository jpa, KafkaOrderEventProducer kafkaProducer) {
        this.springDataOrderRepository = jpa;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        // Step 1: Generate key
        String key = HashUtil.generateIdempotencyKey(
                order.getCustomerName(),
                order.getProduct(),
                order.getQuantity(),
                order.getOrderDate()
        );
        logger.debug("Generated idempotencyKey: {}", key);

        // Step 2: Check if already exists
        Optional<OrderEntity> existing = springDataOrderRepository.findByIdempotencyKey(key);
        if (existing.isPresent()) {
            logger.info("Duplicate order detected for key: {}. Returning existing order.", key);
            return OrderMapper.toDomain(existing.get()); // Duplicate detected
        }

        // Step 3: Set IdempotencyKey and save order object
        OrderEntity entity = OrderMapper.toEntity(order);
        entity.setIdempotencyKey(key);
        logger.info("Saving new order for customer: {}, product: {}", order.getCustomerName(), order.getProduct());
        OrderEntity savedEntity = springDataOrderRepository.save(entity);
        logger.debug("Order saved successfully with ID: {}", savedEntity.getId());

        // Step 4: Create OrderEvent and Publish
        CreateOrderEvent event = new CreateOrderEvent();
        event.setId(savedEntity.getId());
        event.setCustomerName(savedEntity.getCustomerName());
        event.setProduct(savedEntity.getProduct());
        event.setQuantity(savedEntity.getQuantity());
        event.setOrderDate(savedEntity.getOrderDate());

        logger.info("Preparing to publish CreateOrderEvent for Order ID: {} to topic: {}", savedEntity.getId(), topic);
        kafkaProducer.sendMessage(topic, event);
        logger.info("Order published successfully with ID: " + savedEntity.getId());

        return OrderMapper.toDomain(savedEntity);
    }

    @KafkaListener(topics = "payment-completed-topic", groupId = "order-group")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        logger.info("Received PaymentCompletedEvent for orderId: {}", event.getOrderId());

        OrderEntity order = springDataOrderRepository.findById(event.getOrderId())
                .orElseThrow(() -> {
                    logger.error("Order not found for ID: {}", event.getOrderId());
                    return new RuntimeException("Order not found");
                });
        order.setStatus("COMPLETED");
        springDataOrderRepository.save(order);
        logger.info("Order ID: {} marked as COMPLETED", event.getOrderId());
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "order-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        logger.info("Received PaymentFailedEvent for orderId: {}", event.getOrderId());

        OrderEntity order = springDataOrderRepository.findById(event.getOrderId())
                .orElseThrow(() -> {
                    logger.error("Order not found for ID: {}", event.getOrderId());
                    return new RuntimeException("Order not found");
                });
        order.setStatus("CANCELLED"); // Compensating transaction
        springDataOrderRepository.save(order);
        logger.info("Order ID: {} marked as CANCELLED due to payment failure", event.getOrderId());
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return springDataOrderRepository.findById(id).map(OrderMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return springDataOrderRepository.findAll().stream()
                .map(OrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springDataOrderRepository.deleteById(id);
    }
}