package com.quickstore.inventoryservice;

import com.quickstore.inventoryservice.model.Inventory;
import com.quickstore.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class InventoryServiceApplication {

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) throws Exception {
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("iphone_14");
			inventory.setQuantity(100);

			Inventory inventory1 = new Inventory();
			inventory1.setSkuCode("iphone_14_red");
			inventory1.setQuantity(10);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory1);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

}
