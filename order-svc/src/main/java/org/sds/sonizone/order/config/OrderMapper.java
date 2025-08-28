package org.sds.sonizone.order.config;
import org.sds.sonizone.order.adapters.out.persistence.OrderEntity;
import org.sds.sonizone.order.domain.model.Order;
public class OrderMapper {
    public static OrderEntity toEntity(Order order) {
        OrderEntity e = new OrderEntity();
        e.setId(order.getId());
        e.setCustomerName(order.getCustomerName());
        e.setProduct(order.getProduct());
        e.setQuantity(order.getQuantity());
        e.setOrderDate(order.getOrderDate());
        e.setStatus("CREATED");
        return e;
    }

    public static Order toDomain(OrderEntity e) {
        return new Order(
                e.getId(),
                e.getCustomerName(),
                e.getProduct(),
                e.getQuantity(),
                e.getOrderDate(),
                e.getStatus()
        );
    }
}
