package com.iamnana.microservice.payment;

import com.iamnana.microservice.customer.CustomerResponse;
import com.iamnana.microservice.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
