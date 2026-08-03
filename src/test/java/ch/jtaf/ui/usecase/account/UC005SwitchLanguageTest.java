package ch.jtaf.ui.usecase.account;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-005: Switch language.
 * <p>
 * See {@code docs/use_cases/uc-005-switch-language.md}.
 */
@SuppressWarnings("java:S2699")
class UC005SwitchLanguageTest extends AbstractViewTest {

	@Test
	void resultate_eingeben_is_displayed() {
		Locale.setDefault(Locale.GERMAN);
		try {
			login("simon@martinelli.ch", "", List.of(Role.ADMIN));

			navigate(DashboardView.class);

			assertThat(find(Button.class).withText("Resultate eingeben").all()).hasSize(6);
		}
		finally {
			Locale.setDefault(Locale.ENGLISH);
		}
	}

}
