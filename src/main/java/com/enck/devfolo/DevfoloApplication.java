package com.enck.devfolo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
//@EnableWebFlux
public class DevfoloApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevfoloApplication.class, args);
	}

//	@Bean
//	HiddenHttpMethodFilter hiddenHttpMethodFilter() {
//		return new HiddenHttpMethodFilter();
//	}

//	@Bean
//	ResourceLoader resourceLoader(){
//
//		return new ResourceLoader();
//	}


}
