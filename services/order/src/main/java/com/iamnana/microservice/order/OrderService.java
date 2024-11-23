package com.iamnana.microservice.order;

import com.iamnana.microservice.customer.CustomerClient;
import com.iamnana.microservice.exception.BusinessException;
import com.iamnana.microservice.kafka.OrderConfirmation;
import com.iamnana.microservice.kafka.OrderProducer;
import com.iamnana.microservice.orderLine.OrderLineRequest;
import com.iamnana.microservice.orderLine.OrderLineService;
import com.iamnana.microservice.payment.PaymentClient;
import com.iamnana.microservice.payment.PaymentRequest;
import com.iamnana.microservice.product.ProductClient;
import com.iamnana.microservice.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderMapper mapper;
    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;


    @Transactional
    public Integer createOrder(OrderRequest request) {
        // we need to check client exist using open feign
        var customer = this.customerClient.findCustomerById(request.customerId())
                .orElseThrow(()-> new BusinessException("Can't create order:: No customer exist with the provided "));

        // we need to purchase the product using RestTemplate
        var purchasedProducts = this.productClient.purchaseProducts(request.products());

        // now we have to persist the order
        var order = repository.save(mapper.toOrder(request));

        // we need to persist the order lines
        for(PurchaseRequest purchaseRequest : request.products()){
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        // we need to start the payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        // send order confirmation --> notification sms using Kafka
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return this.repository.findAll()
                .stream()
                .map(this.mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return this.repository.findById(orderId)
                .map(this.mapper::fromOrder)
                .orElseThrow(()-> new EntityNotFoundException(String.format("No order found with the provided ID: %d", orderId)));
    }
}
