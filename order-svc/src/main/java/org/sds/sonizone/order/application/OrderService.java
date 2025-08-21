package org.sds.sonizone.order.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sds.sonizone.order.adapters.in.rest.exception.ResourceNotFoundException;
import org.sds.sonizone.order.domain.model.Order;
import org.sds.sonizone.order.domain.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger logger = LogManager.getLogger(OrderService.class);
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

    //@Cacheable(value = "orders", key = "#id")
    public Order getOrderById(UUID id) {
        logger.info("Fetching record from DB for ID: " + id);
        return orderRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    //@CacheEvict(value = "orders", key = "#id")
    public void deleteOrder(UUID id) {
        orderRepository.deleteById(id);
    }

    //@CachePut(value = "orders", key = "#id")
    /*
     TODO: Commented th code for Caching as Getting issue with createOrder and updateOrder like {
       "@class": "java.util.HashMap",
       "error": "Internal Server Error: JSON parse error: Could not resolve subtype of [simple type, class org.sds.sonizone.order.domain.model.Order]: missing type id property '@class'"
         }
     */
    public Order updateOrder(UUID id, Order updatedOrder) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        // Set the ID to ensure updatedOrder matches existing order
        updatedOrder.setId(id);

        // Save and return the updated order
        return orderRepository.save(updatedOrder);
    }
}
