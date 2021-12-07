package com.order.domainlayer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;

import com.order.adapter.dto.PaymentDetails;

import reactor.core.publisher.Mono;

public interface OrderService {

	public Mono<PaymentDetails> save(PaymentDetails _prod);

	public void consumePaymentEvent(ConsumerRecord<String, String> event, @Headers MessageHeaders headers);

}
