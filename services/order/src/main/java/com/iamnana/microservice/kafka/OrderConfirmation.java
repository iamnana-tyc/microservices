package com.iamnana.microservice.kafka;

import com.iamnana.microservice.customer.CustomerResponse;
import com.iamnana.microservice.order.PaymentMethod;
import com.iamnana.microservice.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer, // detail information of customer
        List<PurchaseResponse> products  // list of products customer have purchased
) {
}
