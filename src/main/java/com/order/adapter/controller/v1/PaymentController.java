package com.order.adapter.controller.v1;

import static com.order.APPConstant.V1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.order.adapter.dto.PaymentDetails;
import com.order.adapter.dto.Response;
import com.order.domainlayer.service.OrderService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping(V1)
@CrossOrigin("*")
public class PaymentController {

	
 
}
