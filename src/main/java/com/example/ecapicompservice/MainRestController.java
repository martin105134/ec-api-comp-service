package com.example.ecapicompservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@Slf4j
public class MainRestController {

    @Autowired
    @Qualifier("order-service")
    WebClient webClientOrder;

    @Autowired
    @Qualifier("payment-service")
    WebClient webClientPayment;

    public ResponseEntity<?> getOrders(@PathVariable String orderId) {


    }
}
