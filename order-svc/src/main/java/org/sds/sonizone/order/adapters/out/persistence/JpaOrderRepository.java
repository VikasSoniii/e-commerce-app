package org.sds.sonizone.order.adapters.out.persistence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sds.sonizone.order.adapters.in.rest.OrderController;
import org.sds.sonizone.order.common.HashUtil;
import org.sds.sonizone.order.config.OrderMapper;
import org.sds.sonizone.order.domain.model.Order;
import org.sds.sonizone.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaOrderRepository implements OrderRepository {
    private static final Logger logger = LogManager.getLogger(JpaOrderRepository.class);

    private final SpringDataOrderRepository jpa;

    public JpaOrderRepository(SpringDataOrderRepository jpa) {
        this.jpa = jpa;
    }

    @Override
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
        Optional<OrderEntity> existing = jpa.findByIdempotencyKey(key);
        if (existing.isPresent()) {
            logger.info("Duplicate order detected for key: {}. Returning existing order.", key);
            return OrderMapper.toDomain(existing.get()); // Duplicate detected
        }

        OrderEntity entity = OrderMapper.toEntity(order);
        entity.setIdempotencyKey(key);
        logger.info("Saving new order for customer: {}, product: {}", order.getCustomerName(), order.getProduct());
        OrderEntity savedEntity = jpa.save(entity);
        logger.debug("Order saved successfully with ID: {}", savedEntity.getId());

        return OrderMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpa.findById(id).map(OrderMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return jpa.findAll().stream()
                .map(OrderMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}