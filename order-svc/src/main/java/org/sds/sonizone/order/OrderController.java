package org.sds.sonizone.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @GetMapping("/fetchOrderData")
    public ResponseEntity<String> getOrderDetails(){
        logger.info("starts: Received request to process operation");
        return ResponseEntity.ok("Order details fetched successfully!");
    }
}
