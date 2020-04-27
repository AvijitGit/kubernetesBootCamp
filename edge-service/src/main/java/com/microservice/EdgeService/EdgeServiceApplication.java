package com.microservice.EdgeService;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.CollectionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@EnableFeignClients
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableDiscoveryClient
@EnableZuulProxy
@SpringBootApplication
public class EdgeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdgeServiceApplication.class, args);
	}

}

@Data
class Item {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

/**
* For normal spring microservice call that is not using kubernetes(with out cloud) then there is a important role of eureka server where all the application get registered this is done through (spring.application.name=item-catalog-service) in all the application.properties file
* Of the dependent service. Eureka server know all the IPS of the dependent service and its route the traffic to a particular service using round-robin fashion  
*/
@FeignClient(value = "item-catalog-service", url = "http://item-catalog-service:8763")
interface ItemClient {

	@GetMapping("/items")
	CollectionModel<Item> readItems();
}

@RestController
class GoodItemApiAdapterRestController {

	private final ItemClient itemClient;

	public GoodItemApiAdapterRestController(ItemClient itemClient) {
		this.itemClient = itemClient;
	}

	//@HystrixCommand(fallbackMethod = "failback", commandProperties = {@HystrixProperty(name = "circuitBreaker.forceOpen", value ="true")})
	@GetMapping("/top-brands")
	public Collection<Item> goodItems() {
		return itemClient.readItems()
				.getContent()
				.stream()
				.filter(this::isGreat)
				.collect(Collectors.toList());
	}

	private boolean isGreat(Item item) {
		return !item.getName().equals("Nike") &&
				!item.getName().equals("Adidas") &&
				!item.getName().equals("Reebok");
	}
	public Collection<Item> failback(){
		System.out.println("Hystric called !!!!!!!");
		return new ArrayList<Item>();
	}
}
