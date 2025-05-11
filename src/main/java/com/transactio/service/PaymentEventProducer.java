package com.transactio.service;

import com.transactio.event.PaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

  private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

  @Value("${spring.kafka.topic.payment-events}")
  private String paymentEventsTopic;

  public void sendPaymentEvent(PaymentEvent event) {
    CompletableFuture<SendResult<String, PaymentEvent>> future = kafkaTemplate.send(paymentEventsTopic, event.getPaymentId().toString(), event);

    future.whenComplete((result, ex) -> {
      if (ex == null) {
        log.info("Sent payment event: {} with offset: {}",
            event, result.getRecordMetadata().offset());
      } else {
        log.error("Unable to send payment event: {}", event, ex);
      }
    });
  }
}