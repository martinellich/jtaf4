package ch.jtaf;

import org.springframework.boot.SpringApplication;

public class TestJtaf4Application {

	public static void main(String[] args) {
		SpringApplication.from(Jtaf4Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
