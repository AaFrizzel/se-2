package de.freerider.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
// launch Controllers from package: de.freerider.restapi
basePackages = { "de.freerider.restapi" }
)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.out.println( "Hello, freerider.de" );
	}

}
