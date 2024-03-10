package com.bartoszthielmann.gitapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "BartoszThielmann/gitapi", version = "v1",
		description = "API which allows to process information from multiple GitHub API endpoints into one response"))
public class GitapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(GitapiApplication.class, args);
	}

}
