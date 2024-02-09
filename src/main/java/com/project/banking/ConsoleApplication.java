package com.project.banking;

import com.project.banking.controller.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ConsoleApplication implements CommandLineRunner {
	private final Runner runner;

	@Autowired
	public ConsoleApplication(Runner runner) {
		this.runner = runner;
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(ConsoleApplication.class).web(WebApplicationType.NONE).run(args);
	}

	@Override
	public void run(String... args) {
		runner.run();
	}
}