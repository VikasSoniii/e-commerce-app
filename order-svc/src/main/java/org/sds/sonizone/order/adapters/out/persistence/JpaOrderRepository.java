package org.sds.sonizone.order.adapters.out.persistence;

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

    private final SpringDataOrderRepository jpa;

    public JpaOrderRepository(SpringDataOrderRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        return OrderMapper.toDomain(jpa.save(entity));
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