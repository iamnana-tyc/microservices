package com.iamnana.microservice.orderLine;



public record OrderLineRequest(
        Integer id,
        Integer orderId,
        Integer productId,
        double quantity
) {
}
