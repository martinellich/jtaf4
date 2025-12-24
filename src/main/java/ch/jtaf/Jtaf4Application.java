package ch.jtaf;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@PWA(name = "JTAF 4", shortName = "JTAF 4", description = "JTAF - Track and Field")
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet(Lumo.UTILITY_STYLESHEET)
@StyleSheet("styles.css")
@SpringBootApplication
public class Jtaf4Application implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(Jtaf4Application.class, args);
	}

}
