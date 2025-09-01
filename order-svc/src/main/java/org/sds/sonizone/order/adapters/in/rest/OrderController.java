package org.sds.sonizone.order.adapters.in.rest;


import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.sds.sonizone.order.application.OrderService;
import org.sds.sonizone.order.domain.model.Order;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/orders")
public class OrderController {
    private static final Logger logger = LogManager.getLogger(OrderController.class);
    @Autowired
    private RestTemplate restTemplate;
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    //@PreAuthorize("hasAuthority('SCOPE_admin')") //Only Admin can create the Order TODO: NEED TO CHECK
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        logger.info("starts: Inside createOrder and request data is: " + order);
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    //@PreAuthorize("hasAuthority('SCOPE_internal')")
    @GetMapping("/{id}")
    public Order getById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    public List<Order> getAll() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable UUID id, @RequestBody Order order) {
        return orderService.updateOrder(id, order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Retryable(retryFor = { RestClientException.class },        // Retry on network-related exceptions
               maxAttempts = 4,                                 // Override the global setting, try 3 times total
               backoff = @Backoff(value = 300, multiplier = 1.5)) // 300ms, 450ms, 675ms
    @CircuitBreaker(name = "paymentService",
                    fallbackMethod = "fallbackPayment")
    @GetMapping("/callPaymentSvc")
    public ResponseEntity<String> callPaymentService(){
        System.out.println("Attempting to call payment service for order service."); // LOG HERE

        logger.info("starts: Sending request to payment service.");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8001/payments/fetchPaymentData", String.class);

        //Used when order-svc,payment-svc deployed in k8s cluster
        //ResponseEntity<String> response = restTemplate.getForEntity("http://payment-svc/payment/fetchPaymentData", String.class);
        logger.info("ends: Received response from payment service: {}", response.getBody());
        return response;
    }
    // Fallback method for the Circuit Breaker
    // Fallback method signature must match original method, with an added exception parameter
    // This is called if:
    // 1. The circuit is OPEN, so the call is short-circuited.
    // 2. All retries from @Retryable are exhausted and the call still failed.
    // The exception parameter is MANDATORY.
    public ResponseEntity<String> fallbackPayment(Exception ex){
        System.out.println("FALLBACK TRIGGERED for order: {}. Reason: {}" + ex.getMessage()); // LOG HERE

        // Log the error
        logger.error("Payment service failed: {}", ex.getMessage(), ex);
        // Option 1: Save order state to "PAYMENT_PENDING" for later retry.
        // orderService.updateOrderStatus(orderId, "PAYMENT_PENDING");
        // Option 2: Return a default response.
        return ResponseEntity.ok("Payment service is temporarily unavailable. Please try again later. Order is pending.");
    }

    @PostMapping("/clearCachedData")
    public String clearCache(){
        return orderService.clearAllCache();
    }
}