package com.qortex.inventory;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author RAHUL KUSHWAH
 * @version 1.0
 * @since 23-02-2023
 */

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Inventory management", version = "1.0.0"))
public class InventoryManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryManagementApplication.class, args);
	}

}
