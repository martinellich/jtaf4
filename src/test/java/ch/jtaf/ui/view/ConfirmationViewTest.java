package ch.jtaf.ui.view;

import ch.jtaf.db.tables.records.SecurityUserRecord;
import ch.jtaf.domain.UserAlreadyExistException;
import ch.jtaf.domain.UserService;
import ch.jtaf.ui.ConfirmView;
import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.QueryParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;

@SuppressWarnings("java:S2699")
class ConfirmationViewTest extends KaribuTest {

	@Autowired
	private UserService userService;

	@Test
	void confirmation_successful() throws UserAlreadyExistException {
		SecurityUserRecord user = userService.createUser("Martha", "Keller", "martha.keller@nodomain.xyz", "pass",
				Locale.of("de", "CH"));

		UI.getCurrent().navigate("confirm", new QueryParameters(Map.of("cf", List.of(user.getConfirmationId()))));

		_assert(H1.class, 1, spec -> spec.withText("The confirmation was successful you can now log in."));
	}

	@Test
	void missing_query_parameter() {
		UI.getCurrent().navigate(ConfirmView.class);

		_assert(H1.class, 1, spec -> spec.withText("The confirmation was not successful."));
	}

	@Test
	void invalid_query_parameter() {
		UI.getCurrent().navigate(ConfirmView.class, new QueryParameters(Map.of("cf", List.of("abc"))));

		_assert(H1.class, 1, spec -> spec.withText("The confirmation was not successful."));
	}

}
