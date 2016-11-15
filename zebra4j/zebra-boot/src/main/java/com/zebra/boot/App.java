package com.zebra.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lzy@js-dev.cn on 2016/11/15 0015.
 */
@RestController
@SpringBootApplication
public class App {

	@RequestMapping("/")
	String boot(){
		return " Zebra is booting  ... ";
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
