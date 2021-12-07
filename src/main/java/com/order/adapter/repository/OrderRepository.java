package com.order.adapter.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.order.adapter.dto.PaymentDetails;
import com.order.adapter.entities.OrderDetails;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderRepository extends ReactiveMongoRepository<OrderDetails, String> {

}
