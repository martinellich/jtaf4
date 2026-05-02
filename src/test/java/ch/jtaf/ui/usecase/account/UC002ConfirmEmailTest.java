package ch.jtaf.ui.usecase.account;

import ch.jtaf.db.tables.records.SecurityUserRecord;
import ch.jtaf.domain.UserAlreadyExistException;
import ch.jtaf.domain.UserService;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.ConfirmView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.router.QueryParameters;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-002: Confirm e-mail.
 * <p>
 * See {@code docs/use_cases/uc-002-confirm-email.md}.
 */
class UC002ConfirmEmailTest extends AbstractViewTest {

	@Autowired
	private UserService userService;

	@Test
	void confirmation_successful() throws UserAlreadyExistException {
		setupVaadin();

		SecurityUserRecord user = userService.createUser("Martha", "Keller", "martha.keller@nodomain.xyz", "pass",
				Locale.of("de", "CH"));

		UI.getCurrent().navigate("confirm", QueryParameters.simple(Map.of("cf", user.getConfirmationId())));

		assertThat($(H1.class).withText("The confirmation was successful you can now log in.").all()).hasSize(1);
	}

	@Test
	void missing_query_parameter() {
		setupVaadin();

		navigate(ConfirmView.class);

		assertThat($(H1.class).withText("The confirmation was not successful.").all()).hasSize(1);
	}

	@Test
	void invalid_query_parameter() {
		setupVaadin();

		UI.getCurrent().navigate("confirm", QueryParameters.simple(Map.of("cf", "abc")));

		assertThat($(H1.class).withText("The confirmation was not successful.").all()).hasSize(1);
	}

}
