package net.zeotrope.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ItemServiceApplication {
    static void main(String[] args) {
        SpringApplication.run(ItemServiceApplication.class, args);
    }
}
