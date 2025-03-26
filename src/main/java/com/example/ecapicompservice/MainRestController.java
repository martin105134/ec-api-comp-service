package com.example.ecapicompservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class MainRestController {

    @Autowired
    @Qualifier("order-service")
    WebClient webClientOrder;

    @Autowired
    @Qualifier("payment-service")
    WebClient webClientPayment;

    @Autowired
    @Qualifier("product-service")
    WebClient webClientProduct;

    @Autowired
    CompService compService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/get/order/{orderId}")
    public ResponseEntity<?> getOrders(@PathVariable String orderId, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        String  cookieName = "comp-order-first-stage";
        List<Cookie> cookieList = null;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            cookieList = new ArrayList<>();
        }else{
            cookieList = List.of(cookies);
        }
        Cookie cookieStage1 = new Cookie(cookieName, orderId);
        cookieStage1.setMaxAge(10000);
        if(cookieList.stream().filter(cookie -> cookie.getName().equals(cookieName)).findAny().isEmpty()) {
            log.info("cookie stage 1 found");
            Order order = webClientOrder.get().uri(orderId).retrieve().bodyToMono(Order.class).block();
            Payment payment = webClientPayment.get().uri(orderId).retrieve().bodyToMono(Payment.class).block();
            OrderView orderView = new OrderView();
            orderView.setOrderid(orderId);
            if(payment != null) {
                orderView.setPaymentid(payment.getPaymentId());
                //orderView.setTotalAmount(String.valueOf(payment.getPaymentAmount()));
            }
            orderView.setStatus(order.getOrderStatus());
            ObjectMapper objectMapper = new ObjectMapper();
            String orderViewJson =  objectMapper.writeValueAsString(orderView);
            log.info("orderViewJson ::" + orderViewJson);
            log.info("cookieStage1.getValue() ::" + cookieStage1.getValue());
            compService.setValue(orderView.getOrderid(), "ORDERDATA"+orderViewJson);
            response.addCookie(cookieStage1);
            return ResponseEntity.ok(orderView);
        }else {
            Cookie followup_cookie =  cookieList.stream().
                    filter(cookie -> cookie.getName().equals(cookieName)).findAny().get();
            String followup_cookie_key = followup_cookie.getValue();
            log.info("followup_cookie_key ::" + followup_cookie_key);
            log.info(compService.getRedisValue(followup_cookie_key));
            String cacheResponse =compService.getRedisValue(followup_cookie_key);
            log.debug("cacheResponse ::" + cacheResponse);
            if (cacheResponse == null) {
                return ResponseEntity.ok("Error Processing the Order");
            }
            if(cacheResponse.contains("INITIATED"))
            {
                log.info("Request still under process...");
                return ResponseEntity.ok("Request still under process...");
            }
            else if(cacheResponse.contains("ORDERDATA"))
            {
                log.info("Request already processed");
                ObjectMapper objectMapper = new ObjectMapper();
                OrderView orderView = objectMapper.readValue(cacheResponse.replace("ORDERDATA",""), OrderView.class);
                log.debug("orderView ::" + orderView);
                return ResponseEntity.ok(orderView);
            }
            else
            {
                return ResponseEntity.ok("Error Processing the Order");
            }
        }
    }
}
