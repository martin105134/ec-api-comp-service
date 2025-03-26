package com.example.ecapicompservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.converters.Auto;
import com.stoyanr.evictor.map.EvictibleEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

@Service
@Slf4j
public class KafkaConsumer {

    @Autowired
    CompService compService;

    @KafkaListener(topics = "order-event", groupId = "ec_group_3")
    public void consume(String message) throws JsonProcessingException {
        log.debug("Entering into Kafka Consumer");
        log.info("Consumed message :: " + message);
        ObjectMapper mapper = new ObjectMapper();
        Analytic analytic = mapper.readValue(message, Analytic.class);
        OrderView orderView = new OrderView();
        orderView.setOrderid(analytic.getOrderid());
        orderView.setPaymentid(analytic.getPaymentid());
        orderView.setStatus(analytic.getStatus());
        ObjectMapper objectMapper = new ObjectMapper();
        String orderViewJson =  objectMapper.writeValueAsString(orderView);
        log.info("orderid: "+orderView.getOrderid());
        compService.setValue(orderView.getOrderid(), "ORDERDATA"+orderViewJson);
        log.debug("Exit from Kafka Consumer");
    }

}
