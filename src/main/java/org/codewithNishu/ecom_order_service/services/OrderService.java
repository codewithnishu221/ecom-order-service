package org.codewithNishu.ecom_order_service.services;

import org.codewithNishu.ecom_order_service.client.InventoryClient;
import org.codewithNishu.ecom_order_service.dto.Inventory;
import org.codewithNishu.ecom_order_service.exceptions.MyCustomRuntimeException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    private final RestTemplate restTemplate;
    private final RestClient restClient;
    private final InventoryClient inventoryClient;
    public OrderService(RestTemplate restTemplate, RestClient restClient, InventoryClient inventoryClient){
        this.inventoryClient = inventoryClient;
        this.restTemplate = restTemplate;
        this.restClient = restClient;
    }

    public String placeOrder(Long productId){
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
       Inventory inventory = inventoryClient.getInventory(productId);
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
