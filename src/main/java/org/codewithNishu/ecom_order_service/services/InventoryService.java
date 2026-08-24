package org.codewithNishu.ecom_order_service.services;

import org.codewithNishu.ecom_order_service.client.InventoryClient;
import org.codewithNishu.ecom_order_service.dto.Inventory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;

@Service
public class InventoryService {

    private final InventoryClient inventoryClient;

    public InventoryService(InventoryClient inventoryClient){
        this.inventoryClient = inventoryClient;
    }
    // @Retryable(
    //     retryFor = RuntimeException.class,
    //     maxAttempts = 3,
    //     backoff = @Backoff(delay = 2000)
    // )
    // @RateLimiter(name = "inventoryService", fallbackMethod="fallbackMethod")

    @CircuitBreaker(name = "inventoryServiceCircuitBreaker", fallbackMethod = "circuitBreakerfallbackMethod")
    public Inventory getInventory(Long productId){
        System.out.println("Calling Inventory Service for ProductId: "+ productId);
        return inventoryClient.getInventory(productId);
    }
    public Inventory circuitBreakerfallbackMethod(Long productId, Throwable throwable){
    System.out.println("Fallback executed for productId: " + productId + " due to: " + throwable.getClass().getSimpleName() + " - " + throwable.getMessage());
    return new Inventory(productId, 0);
}
    // public Inventory fallbackMethod(Long productId, Throwable Throwable){
    //     System.out.println("Fallback method called for productId: "+ productId);
    //     return new Inventory(productId, 0);
    // }
}
