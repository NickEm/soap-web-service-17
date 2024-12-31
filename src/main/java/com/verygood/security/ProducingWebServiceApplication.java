package com.verygood.security;

import java.security.Security;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProducingWebServiceApplication {

	static {
		// Re-enable sha1 algorithms
		String value = Security.getProperty("jdk.xml.dsig.secureValidationPolicy");
		value = Arrays.stream(value.split(","))
			.filter(v -> !v.contains("sha1"))
			.collect(Collectors.joining(","));
		Security.setProperty("jdk.xml.dsig.secureValidationPolicy", value);
	}

	public static void main(String[] args) {
		SpringApplication.run(ProducingWebServiceApplication.class, args);
	}

}
