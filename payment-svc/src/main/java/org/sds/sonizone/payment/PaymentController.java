package org.sds.sonizone.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @GetMapping("/fetchPaymentData")
    public ResponseEntity<String> getPaymentDetails(){
        logger.info("starts: Received request to process payment data");
        return ResponseEntity.ok("Payment details fetched successfully!");
    }
}
