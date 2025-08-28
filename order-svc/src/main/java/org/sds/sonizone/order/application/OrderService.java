package org.sds.sonizone.order.application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sds.sonizone.order.adapters.in.rest.exception.ResourceNotFoundException;
import org.sds.sonizone.order.domain.model.Order;
import org.sds.sonizone.order.domain.repository.OrderRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger logger = LogManager.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final CacheManager cacheManager;

    public OrderService(CacheManager cacheManager,OrderRepository orderRepository) {
        this.cacheManager = cacheManager;
        this.orderRepository = orderRepository;
    }

    // CREATE - Add @CachePut to cache the newly created order
    @CachePut(value = "orders", key = "#order.id")
    public Order createOrder(Order order) {
        logger.info("Creating new order");
        if (order.getId() == null) {
            order.setId(UUID.randomUUID());
        }
        return orderRepository.save(order);
    }

    // READ - Single order with caching
    @Cacheable(value = "orders", key = "#id")
    public Order getOrderById(UUID id) {
        logger.info("Fetching record from DB for ID: {}", id);
        return orderRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    public List<Order> getAllOrders() {
        logger.info("Fetching all orders from DB");
        return orderRepository.findAll();
    }

    // UPDATE - Update cache and database
    @CachePut(value = "orders", key = "#id")
    public Order updateOrder(UUID id, Order updatedOrder) {
        logger.info("Updating order with ID: {}", id);
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        // Set the ID to ensure updatedOrder matches existing order
        updatedOrder.setId(id);

        // Save and return the updated order
        return orderRepository.save(updatedOrder);
    }

    // DELETE - Remove from cache and database
    @CacheEvict(value = "orders", key = "#id")
    public void deleteOrder(UUID id) {
        logger.info("Deleting order with ID: {}", id);
        // Check if order exists first to avoid unnecessary cache eviction if not found
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        orderRepository.deleteById(id);
    }


    // Optional: Clear all cache (useful for admin operations)
    @Caching(evict = {
            @CacheEvict(value = "orders", allEntries = true),
            @CacheEvict(value = "allOrders", allEntries = true) // If you cache getAllOrders
    })
    public String clearAllCache() {
        logger.info("Clearing all order caches");
        // This method only clears cache, no DB operation
        // Clear all caches managed by CacheManager
        cacheManager.getCacheNames().forEach(cacheName -> {
            logger.debug("Clearing cache: {}", cacheName);
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                logger.debug("Cache {} cleared successfully", cacheName);
            }
        });

        logger.info("All caches cleared successfully");
        return "All caches cleared successfully";
    }

    // Optional: Additional cacheable method for all orders
    @Cacheable(value = "allOrders")
    @Transactional(readOnly = true)
    public List<Order> getAllOrdersCached() {
        logger.info("Fetching all orders from DB (cached)");
        return orderRepository.findAll();
    }
}