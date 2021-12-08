package com.order.adapter.controller.v1;

import static com.order.APPConstant.V1;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.adapter.dto.Response;
import com.order.adapter.entities.OrderDetails;
import com.order.domainlayer.service.OrderService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(V1)
@CrossOrigin("*")
public class OrderDetailsController {

	@Autowired
	private OrderService svc;

	@GetMapping(value = "/")
	public Mono<ResponseEntity<Response<List<OrderDetails>>>> allProducts() {

		return svc.fetchOrderDetails().collectList()
				.map(list -> new ResponseEntity<Response<List<OrderDetails>>>(
						new Response<List<OrderDetails>>(true, "Record retrieved successully", list), HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<Response<List<OrderDetails>>>(
						new Response<List<OrderDetails>>(false, "Record not found"), HttpStatus.NOT_FOUND));

	}
	
	@GetMapping(value = "/checkout")
	public Mono<ResponseEntity<Response<OrderDetails>>> fetchCheckOutOrderDetails() {

		return svc.fetchCheckOutOrderDetails()
				.map(list -> new ResponseEntity<Response<OrderDetails>>(
						new Response<OrderDetails>(true, "Record retrieved successully", list), HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<Response<OrderDetails>>(
						new Response<OrderDetails>(false, "Record not found"), HttpStatus.NOT_FOUND));

	}
}
