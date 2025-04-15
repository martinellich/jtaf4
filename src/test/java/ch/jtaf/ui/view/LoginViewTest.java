package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.KaribuTest;
import ch.jtaf.ui.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginOverlay;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LoginFormKt._login;
import static org.assertj.core.api.Assertions.assertThat;

class LoginViewTest extends KaribuTest {

	@Test
	void login_with_unknown_user() {
		UI.getCurrent().navigate(LoginView.class);

		try {
			_login(_get(LoginOverlay.class), "not.existing@user.com", "pass");
		}
		catch (IllegalStateException e) {
			// From GoogleAnalyticsTracker. Ignore
		}

		assertThat(_get(LoginOverlay.class).getElement().getOuterHTML())
			.isEqualTo("<vaadin-login-overlay></vaadin-login-overlay>");
	}

	@Test
	void already_logged_in() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		UI.getCurrent().navigate(LoginView.class);

		H1 h1 = _get(H1.class, spec -> spec.withId("view-title"));
		assertThat(h1.getText()).isEqualTo("Dashboard");
	}

}
