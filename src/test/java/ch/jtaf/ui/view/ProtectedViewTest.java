package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.ClubsView;
import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class ProtectedViewTest extends KaribuTest {

	@Test
	void user_without_organization() {
		// This user has no organization
		login("susan.miller@mail.com", "pass", List.of(Role.ADMIN));

		UI.getCurrent().navigate(ClubsView.class);

		// Assert that the user was rerouted to organization
		H1 h1 = _get(H1.class, spec -> spec.withId("view-title"));
		assertThat(h1.getText()).isEqualTo("Organizations");
	}

}
