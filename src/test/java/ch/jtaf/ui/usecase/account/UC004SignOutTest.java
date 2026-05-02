package ch.jtaf.ui.usecase.account;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import com.vaadin.flow.component.button.Button;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-004: Sign out.
 * <p>
 * See {@code docs/use_cases/uc-004-sign-out.md}.
 */
class UC004SignOutTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	void logout_button_visible_when_signed_in() {
		Button logout = $(Button.class).id("logout");
		assertThat(logout.isVisible()).isTrue();
		assertThat(logout.getText()).contains("simon@martinelli.ch");

		try {
			logout.click();
		}
		catch (Exception _) {
			// VaadinServletRequest is unavailable in unit tests, while the
			// production logout flow runs through Spring Security.
		}
	}

}
