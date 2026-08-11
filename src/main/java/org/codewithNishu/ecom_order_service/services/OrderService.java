package org.codewithNishu.ecom_order_service.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    private final RestTemplate restTemplate;

    public OrderService(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public String placeOrder(String productId){
       String response =  restTemplate.getForObject("http://localhost:8081/inventory/" + productId, String.class);
       return "IN STOCK".equals(response) ? "Order placed Successfully": "Product out of stock";
        
    }

}
