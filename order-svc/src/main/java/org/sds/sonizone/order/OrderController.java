package org.sds.sonizone.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/fetchOrderData")
    public ResponseEntity<String> getOrderDetails(){
        logger.info("starts: Received request to process operation");
        return ResponseEntity.ok("Order details fetched successfully!");
    }

    @GetMapping("/callPaymentSvc")
    public ResponseEntity<String> callPaymentService(){
        logger.info("starts: Sending request to payment service.");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8001/payment/fetchPaymentData", String.class);
        logger.info("ends: Received response from payment service: {}", response.getBody());
        return response;
    }
}