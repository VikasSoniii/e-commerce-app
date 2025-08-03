package org.sds.sonizone.common.events;

import java.util.UUID;

public class PaymentFailedEvent {
    private UUID orderId;
    private String reason; // e.g., "Insufficient balance"

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
