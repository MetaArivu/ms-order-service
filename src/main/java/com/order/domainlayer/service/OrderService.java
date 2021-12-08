package com.order.domainlayer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;

import com.order.adapter.entities.OrderDetails;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService {

	public Flux<OrderDetails> fetchOrderDetails();
	
	public void consumePaymentEvent(ConsumerRecord<String, String> event, @Headers MessageHeaders headers);

	public void consumeCartEvent(ConsumerRecord<String, String> event, MessageHeaders headers);

	public Mono<OrderDetails> fetchCheckOutOrderDetails();

}
