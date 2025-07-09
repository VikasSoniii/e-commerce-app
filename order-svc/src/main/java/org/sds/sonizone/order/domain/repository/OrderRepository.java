package org.sds.sonizone.order.domain.repository;

import org.sds.sonizone.order.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
    void deleteById(UUID id);
}