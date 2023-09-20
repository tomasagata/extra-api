package org.mojodojocasahouse.extra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication (exclude = SecurityAutoConfiguration.class)
public class ExtraApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExtraApplication.class, args);
	}

}
