package org.sds.sonizone.payment.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private static final Logger logger = LogManager.getLogger(PaymentController.class);

    @GetMapping("/fetchPaymentData")
    public ResponseEntity<String> getPaymentDetails(){
        logger.info("starts: Received request to process payment data");
        return ResponseEntity.ok("Payment details fetched successfully!");
    }
}
