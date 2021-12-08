package com.order.adapter.service;

import static com.order.APPConstant.KAFKA_TOPIC_CART_AGGREGATE_EVENT;
import static com.order.APPConstant.KAFKA_TOPIC_PAYMENT_EVENT;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.order.adapter.dto.PaymentDetails;
import com.order.adapter.dto.ShoppingCart;
import com.order.adapter.entities.OrderDetails;
import com.order.adapter.repository.OrderRepository;
import com.order.domainlayer.service.OrderService;
import com.order.server.config.AppProperties;
import com.order.server.secutiry.JWTUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderServiceImpl implements OrderService {

	private static final Logger log = (Logger) LoggerFactory.getLogger(OrderServiceImpl.class);

	@Autowired
	private OrderRepository repo;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AppProperties appProp;

	@Autowired
	private JWTUtil jwtUtil;

	@Override
	public Flux<OrderDetails> fetchOrderDetails() {
		String authHeader = MDC.get("Authorization");
		String userId = jwtUtil.getUserIdFromToken(authHeader);
		return repo.findByCustomerId(userId);
	}
	
	@Override
	public Mono<OrderDetails> fetchCheckOutOrderDetails() {
		String authHeader = MDC.get("Authorization");
		String userId = jwtUtil.getUserIdFromToken(authHeader);
		return repo.findByCustomerIdAndStageAndActive(userId, 1, true);
	}

	@Override
	@KafkaListener(topics = { KAFKA_TOPIC_CART_AGGREGATE_EVENT })
	public void consumeCartEvent(ConsumerRecord<String, String> event, @Headers MessageHeaders headers) {
		log.info("Shopping Cart Event Received key={}, offset={}, partition={}", event.key(), event.offset(), event.partition());
		log.info("Shopping Cart Message={}", event.value());
		ShoppingCart cart = ShoppingCart.parse(event.value());
		if(cart!=null && cart.isInCheckOutStage()) {

			repo.findByCustomerIdAndStageAndActive(cart.getCustomerId(), 1, true)
				.subscribe(od ->{
					od.setActive(false);
					repo.save(od)
						.subscribe(_od->{
							log.info("Deactived Previous Order Which Was in Check Out Stage, ID={}",_od.getId());
						});
				});
			
			OrderDetails orderDetails = OrderDetails.build()
					.customerId(cart.getCustomerId())
					.lineItems(cart.getLineItems())
					.stage(1)
					.paymentStatus(false)
					.total(cart.getTotal())
					.build();
			log.info("Order Details={}",orderDetails);
			repo.save(orderDetails).subscribe(od->{
				log.info("Order Created, Id={}, OrderNo={}", od.getId(), od.getOrderNo());
			});
			
		}
	}
	
	@Override
	@KafkaListener(topics = { KAFKA_TOPIC_PAYMENT_EVENT })
	public void consumePaymentEvent(ConsumerRecord<String, String> event, @Headers MessageHeaders headers) {
		log.info("Payment Event Received key={}, offset={}, partition={}", event.key(), event.offset(),
				event.partition());
		log.info("Shopping Cart Message={}", event.value());
		String authHeader = "";
		for (Iterator<String> iterator = headers.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			Object value = headers.get(key);
			if (key.equals("Authorization")) {
				authHeader = new String((byte[]) value);
				log.info("3: {}: {}", key, authHeader);
			}
		}

		if (event.value() != null && jwtUtil.validateBearerToken(authHeader)) {
			PaymentDetails paymentDetails = PaymentDetails.parse(event.value());
			paymentDetails.setAuthorization(authHeader);
			log.info("Payment Details ={}", paymentDetails);
			log.info("Payment Order Id ={}, Status={}",paymentDetails.getOrderId(), paymentDetails.isStatus());
			repo.findById(paymentDetails.getOrderId())
				.subscribe(od->{
					log.info("OD={}",od);
					od.paymentId(paymentDetails.getId())
					  .paymentStatus(paymentDetails.isStatus())
					  .cartId(paymentDetails.getCartId())
					  .authorization(paymentDetails.getAuthorization());
					log.info("OD Before Save={}",od);
					repo.save(od)
						.subscribe(newod->{
							log.info("Order Details Updated={}",newod);
							this.clearCart(paymentDetails.getAuthorization());
						});
				});
		} else {
			log.error("Invalid Authorization Token={}", authHeader);
		}

	}

	private void updateOrderDetails(OrderDetails od) {
		this.clearCart(od.getAuthorization());
		/*
		log.info("Order Created Id={} For Customer={}", od.getId(), od.getCustomerId());
		List<LinkedHashMap> data = this.fetchCartDetails(od.getAuthorization());
		od.addLineItems(data);

		repo.save(od).subscribe((_od) -> {
			log.info("Attached Line Items To Order Detals {}", _od);
			this.clearCart(od.getAuthorization());
		});*/
	}

	private List<LinkedHashMap> fetchCartDetails(String authHeader) {
		try {
			String url = appProp.getQueryCartUrl();
			log.info("Fetch Cart URL={}", url);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", authHeader);
			HttpEntity httpEntity = new HttpEntity<>(headers);

			ResponseEntity<com.order.adapter.dto.Response> responseEntity = this.restTemplate.exchange(url,
					HttpMethod.GET, httpEntity, com.order.adapter.dto.Response.class);

			com.order.adapter.dto.Response response = responseEntity.getBody();
			log.info("Cart Details = {}", response.toJSON());

			if (response.isSuccess()) {
				return (List<LinkedHashMap>) ((List<LinkedHashMap>) response.getData()).get(0).get("lineItems");
			}
			return null;
		} catch (Exception e) {
			log.error("Cart Details Exception = {}", e.getMessage());
			return null;
		}
	}

	private void clearCart(String authHeader) {
		try {
			String userId = jwtUtil.getUserIdFromToken(authHeader);
			LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
			data.put("customerId", userId);
			String url = appProp.getClearCartUrl();
			log.info("Clear Cart URL={}", url);
			log.info("Clear Cart Data={}", data);
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", authHeader);
			HttpEntity httpEntity = new HttpEntity<>(data, headers);

			ResponseEntity<com.order.adapter.dto.Response> responseEntity = this.restTemplate.exchange(url,
					HttpMethod.POST, httpEntity, com.order.adapter.dto.Response.class);

			com.order.adapter.dto.Response response = responseEntity.getBody();
			log.info("Clear Cart Details = {}", response.toJSON());

		} catch (Exception e) {
			log.error("Clear Cart Details Exception = {}", e.getMessage());
		}
	}

}
