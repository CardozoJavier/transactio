package com.transactio.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
  @Value("${spring.kafka.topic.payment-events}")
  private String paymentEventsTopic;

  @Bean
  public NewTopic paymentEventsTopic() {
    return TopicBuilder
        .name(paymentEventsTopic)
        .partitions(3)
        .replicas(1)
        .build();
  }
}
