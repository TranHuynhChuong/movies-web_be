package com.movieweb.movieweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {
		RedisAutoConfiguration.class,
		RedisRepositoriesAutoConfiguration.class
})

@EnableCaching
public class MoviewebApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviewebApplication.class, args);
	}

}
