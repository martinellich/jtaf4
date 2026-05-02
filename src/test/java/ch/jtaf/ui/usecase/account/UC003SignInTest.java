package ch.jtaf.ui.usecase.account;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginOverlay;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-003: Sign in.
 * <p>
 * See {@code docs/use_cases/uc-003-sign-in.md}.
 */
class UC003SignInTest extends AbstractViewTest {

	@Test
	void login_with_unknown_user() {
		setupVaadin();

		navigate(LoginView.class);

		LoginOverlay loginOverlay = $(LoginOverlay.class).single();

		try {
			test(loginOverlay).login("not.existing@user.com", "pass");
		}
		catch (IllegalStateException _) {
			// From GoogleAnalyticsTracker. Ignore
		}

		assertThat($(LoginOverlay.class).single().getElement().getOuterHTML())
			.isEqualTo("<vaadin-login-overlay></vaadin-login-overlay>");
	}

	@Test
	void already_logged_in() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		UI.getCurrent().navigate(LoginView.class);

		H1 h1 = $(H1.class).id("view-title");
		assertThat(h1.getText()).isEqualTo("Dashboard");
	}

}
