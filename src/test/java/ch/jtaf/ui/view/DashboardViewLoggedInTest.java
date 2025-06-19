package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

@SuppressWarnings("java:S2699")
class DashboardViewLoggedInTest extends KaribuTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
	}

	@Test
	void enter_results_is_enabled() {
		UI.getCurrent().getPage().reload();

		_assert(Button.class, 6, spec -> spec.withText("Enter Results"));

		try {
			_get(Button.class, spec -> spec.withId("logout")).click();
		}
		catch (Exception ignored) {
			// https://github.com/mvysny/karibu-testing/issues/148
		}
	}

	@Test
	void resultate_eingeben_is_displayed() {
		Locale.setDefault(Locale.GERMAN);

		UI.getCurrent().getPage().reload();

		_assert(Button.class, 6, spec -> spec.withText("Resultate eingeben"));

		Locale.setDefault(Locale.ENGLISH);
	}

}
