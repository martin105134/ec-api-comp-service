package com.example.ecapicompservice;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;


@Getter
@Setter
public class Order {

    private String orderId;
    private String orderStatus;
    private Integer orderTotal;
    private Map<String, Integer> products; // list of dish_id, quantity
    private String orderCustomer;
    private String paymentId;
}
