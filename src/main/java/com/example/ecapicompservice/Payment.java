package com.example.ecapicompservice;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Payment {
    private String paymentId;
    private String orderId;
    private String paymentStatus;
    private Integer paymentAmount;
}
