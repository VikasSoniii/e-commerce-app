package org.sds.sonizone.common.events;

import java.util.UUID;

public class PaymentCompletedEvent {
    private UUID orderId;
    private String status; // e.g., "COMPLETED"

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
