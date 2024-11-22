package com.iamnana.microservice.orderLine;

import com.iamnana.microservice.order.Order;
import org.springframework.stereotype.Service;

@Service
public class OrderLineMapper{
    public OrderLine toOrderLine(OrderLineRequest request) {
        return OrderLine.builder()
                .id(request.id())
                .productId(request.productId())
                .order(Order.builder()
                        .id(request.orderId())
                        .build()
                )
                .quantity(request.quantity())
                .build();
    }

    public OrderLineResponse toOrderLineResponse(OrderLine orderLine) {
        return new OrderLineResponse(
                orderLine.getId(),
                orderLine.getQuantity()
        );
    }
}
