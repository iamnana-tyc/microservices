package com.iamnana.microservice.kafka;

import com.iamnana.microservice.kafka.order.OrderConfirmation;
import com.iamnana.microservice.kafka.payment.PaymentConfirmation;
import com.iamnana.microservice.notification.Notification;
import com.iamnana.microservice.notification.NotificationRepository;
import com.iamnana.microservice.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static java.lang.String.format;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationRepository repository;

    @KafkaListener(topics = "payment-topic")
    public void consumerPaymentSuccessNotification(PaymentConfirmation paymentConfirmation){
        log.info(format("Consuming the message from payment topic Topic:: %s", paymentConfirmation));
        repository.save(
                Notification.builder()
                        .type(NotificationType.PAYMENT_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .paymentConfirmation(paymentConfirmation)
                        .build()
        );

        // todo send an email
    }

    @KafkaListener(topics = "order-topic")
    public void consumerOrderConfirmationNotification(OrderConfirmation orderConfirmation){
        log.info(format("Consuming the message from payment topic Topic:: %s", orderConfirmation));
        repository.save(
                Notification.builder()
                        .type(NotificationType.ORDER_CONFIRMATION)
                        .notificationDate(LocalDateTime.now())
                        .orderConfirmation(orderConfirmation)
                        .build()
        );
    }
}
