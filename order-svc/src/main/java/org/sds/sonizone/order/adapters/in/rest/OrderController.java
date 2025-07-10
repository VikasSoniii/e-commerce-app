package org.sds.sonizone.order.adapters.in.rest;

import org.sds.sonizone.order.adapters.in.rest.exception.ResourceNotFoundException;
import org.sds.sonizone.order.application.OrderService;
import org.sds.sonizone.order.domain.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable UUID id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    @GetMapping
    public List<Order> getAll() {
        return orderService.getAllOrders();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable UUID id, @RequestBody Order order) {
        return orderService.updateOrder(id, order)
                .map(ResponseEntity::ok)
                .orElseThrow(()-> new ResourceNotFoundException("Order not found with ID: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/callPaymentSvc")
    public ResponseEntity<String> callPaymentService(){
        logger.info("starts: Sending request to payment service.");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8001/payment/fetchPaymentData", String.class);

        //Used when order-svc,payment-svc deployed in k8s cluster
        //ResponseEntity<String> response = restTemplate.getForEntity("http://payment-svc/payment/fetchPaymentData", String.class);
        logger.info("ends: Received response from payment service: {}", response.getBody());
        return response;
    }
}