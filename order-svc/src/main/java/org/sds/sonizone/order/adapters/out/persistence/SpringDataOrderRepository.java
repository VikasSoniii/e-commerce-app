package org.sds.sonizone.order.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataOrderRepository extends JpaRepository<OrderEntity, UUID> {
    Optional<OrderEntity> findByIdempotencyKey(String idempotencyKey);
}
