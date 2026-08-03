package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.ClubsView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProtectedViewTest extends AbstractViewTest {

	@Test
	void user_without_organization() {
		// This user has no organization
		login("susan.miller@mail.com", "pass", List.of(Role.ADMIN));

		UI.getCurrent().navigate(ClubsView.class);

		// Assert that the user was rerouted to organization
		H1 h1 = find(H1.class).id("view-title");
		assertThat(h1.getText()).isEqualTo("Organizations");
	}

}
