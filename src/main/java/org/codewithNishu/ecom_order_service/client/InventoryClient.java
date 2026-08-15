package org.codewithNishu.ecom_order_service.client;

import org.codewithNishu.ecom_order_service.config.InventoryFeignClientConfig;
import org.codewithNishu.ecom_order_service.dto.Inventory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", 
url="http://localhost:8082",
configuration = InventoryFeignClientConfig.class
   
)
public interface InventoryClient {

    @GetMapping("/invewntory/{productId}")
    Inventory getInventory(@PathVariable Long productId);

    @PostMapping("/inventory")
    String updatedInventory(@RequestBody Inventory inventory);
}
