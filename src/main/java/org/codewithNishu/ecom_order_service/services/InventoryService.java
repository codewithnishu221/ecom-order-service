package org.codewithNishu.ecom_order_service.services;

import java.util.concurrent.CompletableFuture;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.codewithNishu.ecom_order_service.client.InventoryClient;
import org.codewithNishu.ecom_order_service.dto.Inventory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;

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
    // -------------------- RateLimiter -------------------------------

    // @RateLimiter(name = "inventoryService", fallbackMethod="fallbackMethod")
    // -------------------- CircuitBreaker -------------------------------

    // @CircuitBreaker(name = "inventoryServiceCircuitBreaker", fallbackMethod = "circuitBreakerfallbackMethod")

    // -------------------- TimeLimiter -------------------------------
//    @TimeLimiter(name = "inventoryServiceTimeLimiter", fallbackMethod = "timeLimiterFallbackMethod")
//    public CompletableFuture<Inventory> getInventory(Long productId){// this belongs to time limiter

  // -----Bulkhead---- SEMAPHORE type is defualt--
//   @Bulkhead(name = "inventoryServiceBulkhead",
//   fallbackMethod = "bulkHeadFallbackMethod")
//    public Inventory getInventory(Long productId){
//        System.out.println("Calling Inventory Service for ProductId: "
//                + productId + " Thread: "+ Thread.currentThread().getName());
//        return inventoryClient.getInventory(productId); // this can be used by circutit breaker and other patterns but for TimeLimiter it can't be work
////        return CompletableFuture.supplyAsync(()
////            -> inventoryClient.getInventory(productId));// this belongs to time limiter
//
//    }

// -----Bulkhead---- THREADPOOL type is aqnd its basically design for asynchronous processing
@Bulkhead(name = "inventoryThreadPool",
 type = Bulkhead.Type.THREADPOOL, fallbackMethod = "bulkHeadFallbackMethod")
public  CompletableFuture<Inventory> getInventory(Long productId){
    return CompletableFuture.completedFuture(
            inventoryClient.getInventory(productId)
    );
}

    public CompletableFuture<Inventory> bulkHeadFallbackMethod(Long productId, Throwable throwable){
        System.out.println("Bulkhead rejected request for productId: "
                + productId + " | Reason: " + throwable.getClass().getSimpleName() + " - " + throwable.getMessage());
        Inventory inventory = new Inventory(productId, 0);
        return CompletableFuture.completedFuture(inventory);
    }
    // -----Bulkhead---- SEMAPHORE type is fallback--

//    public Inventory bulkHeadFallbackMethod(Long productId, Throwable throwable){
//        System.out.println("Bulkhead rejected request for productId: "
//                + productId + " | Reason: " + throwable.getClass().getSimpleName() + " - " + throwable.getMessage());
//         return new Inventory(productId, 0);
//    }

    // -------------------- TimeLimiter -------------------------------

//    public CompletableFuture<Inventory> timeLimiterFallbackMethod(Long productId, Throwable throwable){
//    System.out.println("Fallback executed for productId: " + productId + " due to: " + throwable.getClass().getSimpleName() + " - " + throwable.getMessage());
//    // return new Inventory(productId, 0);
//    Inventory inventory = new Inventory(productId, 0);
//    return CompletableFuture.completedFuture(inventory);
//}

    // -------------------- CircuitBreaker -------------------------------

//     public Inventory circuitBreakerfallbackMethod(Long productId, Throwable throwable){
//     System.out.println("Fallback executed for productId: " + productId + " due to: " + throwable.getClass().getSimpleName() + " - " + throwable.getMessage());
//     return new Inventory(productId, 0);
// }
    // public Inventory fallbackMethod(Long productId, Throwable Throwable){
    //     System.out.println("Fallback method called for productId: "+ productId);
    //     return new Inventory(productId, 0);
    // }
}
