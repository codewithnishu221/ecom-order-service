package org.codewithNishu.ecom_order_service.services;


import org.codewithNishu.ecom_order_service.client.InventoryClient;
import org.codewithNishu.ecom_order_service.dto.Inventory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import org.springframework.cloud.client.discovery.DiscoveryClient;
@Service
public class OrderService {

    private final RestTemplate restTemplate;
    private final RestClient restClient;
    private final InventoryClient inventoryClient;
    private final InventoryService inventoryService;
    private final DiscoveryClient discoveryClient; // if we want use eureka service in rest client and rest template because feign is directly try to find the eureka server 
    public OrderService(RestTemplate restTemplate, RestClient restClient, InventoryClient inventoryClient, DiscoveryClient discoveryClient, InventoryService inventoryService){
        this.inventoryClient = inventoryClient;
        this.restTemplate = restTemplate;
        this.restClient = restClient;
        this.discoveryClient = discoveryClient;
        this.inventoryService = inventoryService;
    }

    public String placeOrder(Long productId){
        // if we want to use the rest client & rest template we need to use the discovery client for connect with eureka server and there is an issue with if we have multiple endpoints and we add 0th endpoint then for all request we are getting the 1st url and to resolve this we need to handle the manual load balancer here that is the problem whihc is solved by the feign client so no need to doing this all things 
    //    List<ServiceInstance> instances = discoveryClient.getInstances("ecom-inventory-service");
    //    ServiceInstance serviceInstance =instances.get(0);
    //    URI uri = serviceInstance.getUri();
         // Rest teplate is deprecated by Spring new way is rest Clinet for inter service communication from one service to another service
    //    String response =  restTemplate.getForObject("http://localhost:8081/inventory/" + productId, String.class);
    //    return "IN STOCK".equals(response) ? "Order placed Successfully": "Product out of stock";
        // this is the post restclient with retrieve and error handling 
     /* 
     /  ResponseEntity<Inventory> entity = restClient.get()
       .uri("http://localhost:8081/inventory/{productId}",productId)
       .retrieve()
       .onStatus(HttpStatusCode::is4xxClientError, ((request, response)-> {
        throw new MyCustomRuntimeException(response.getStatusCode(), response.getHeaders());
       }))
       .toEntity(Inventory.class);
       
        updateInventory(entity.getBody());
       return entity.getBody()!= null && entity.getBody().getQuantity()>0 ? "Order placed Successfully": "Product out of stock";*/
       // Call inventory service to get inventory using feign client 
       Inventory inventory = inventoryService.getInventory(productId).join();
       int quantity = inventory.getQuantity();
       updateInventory(inventory);
       return quantity>0 ?
       "Order placed Successfully": "Product out of stock";
       // here we are using the exchange method 
       
       // for exchange method we need to covert our reponse into pet that is difficult 
    //    Pet entity = restClient.get()
    //    .uri("http://localhost:8081/inventory/{productId}",productId)
    //    .accept(APPLICATION_JSON)
    //    .exchange((request, response) -> {
    //     if(response.getStatusCode().is4xxClientError()){
    //         throw new MyCustomRuntimeException(response.getStatusCode(), response.getHeaders())
    //     } else{
    //         Pet pet = convertResponse(response);
    //         return pet;
    //     }
    //    });
      
    }

    private void updateInventory(Inventory inventory){
       inventory.setQuantity(inventory.getQuantity()-1);
       // here is the feign client interservice communication
       inventoryClient.updatedInventory(inventory);
       // this is the post call to inventiry service using  restclient
        // restClient.post()
        // .uri("http://localhost:8081/inventory")
        // .body(inventory)
        // .retrieve()
        // .toBodilessEntity();
    }

}
