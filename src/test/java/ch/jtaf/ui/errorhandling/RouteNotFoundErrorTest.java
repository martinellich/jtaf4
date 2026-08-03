package ch.jtaf.ui.errorhandling;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RouteNotFoundErrorTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
	}

	@Test
	void navigate_to_unknown_route() {
		UI.getCurrent().navigate("unknown");

		H1 h1 = find(H1.class).id("view-title");
		assertThat(h1.getText()).isEqualTo("Dashboard");
	}

}
