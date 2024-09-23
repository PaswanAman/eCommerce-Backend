package com.zosh.ecommerce.schedular;

import com.zosh.ecommerce.service.ProductService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpirationSchedular {

    @Autowired
    private ProductService productService;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional// after one minute check
    public void updateExpiredProducts(){
        System.out.println("Scheduler is checking for expired products at " + java.time.LocalDateTime.now());
        productService.updateExpiredProduct();
    }
}
