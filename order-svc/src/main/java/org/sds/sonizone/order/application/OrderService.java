package org.sds.sonizone.order.application;

import org.sds.sonizone.order.domain.model.Order;
import org.sds.sonizone.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID());
        }
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public void deleteOrder(UUID id) {
        orderRepository.deleteById(id);
    }

    public Optional<Order> updateOrder(UUID id, Order updatedOrder) {
        return orderRepository.findById(id).map(existing -> {
            updatedOrder.setId(id);
            return orderRepository.save(updatedOrder);
        });
    }
}
