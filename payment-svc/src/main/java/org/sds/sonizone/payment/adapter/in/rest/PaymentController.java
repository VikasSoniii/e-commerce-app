package org.sds.sonizone.payment.adapter.in.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    private static final Logger logger = LogManager.getLogger(PaymentController.class);


    /* Method 1
    @GetMapping("/fetchPaymentData")
    public ResponseEntity<String> getPaymentDetails(){
        logger.info("starts: Received request to process payment data");
        return ResponseEntity.ok("Payment details fetched successfully!");
    }*/

    //Method 2: This Api only accessed by other service, and not accessed directly
    @PreAuthorize("hasAuthority('SCOPE_internal') || hasAuthority('SCOPE_admin')")  //TODO: CHECK admin scope
    @GetMapping("/fetchPaymentData")
    public ResponseEntity<String> getPaymentDetails(){
        logger.info("starts: Received request to process payment data");
        return ResponseEntity.ok("Payment details fetched successfully!");
    }

    //ACCESS DIRECTLY
    @GetMapping("/fetchPaymentDataTest")
    public ResponseEntity<String> getPaymentDetailsTest(){
        logger.info("starts: Received request to process payment data");
        return ResponseEntity.ok("Payment details fetched successfully!");
    }

    @PreAuthorize("hasAuthority('SCOPE_admin')") //TODO: CHECK admin scope
    @GetMapping("/admin")
    public ResponseEntity<String> accessByAdmin(){
        logger.info("starts: Received request to access data by admin");
        return ResponseEntity.ok("Access successfully by the admin!");
    }

    @GetMapping("/test")
    public ResponseEntity<String> testPayment(){
        logger.info("starts: Test Payment Service is up or not.");
        return ResponseEntity.ok("Payment service is up and running.");
    }
}
