package ch.jtaf.ui.usecase.account;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.configuration.security.SecurityContext;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.LoginView;
import ch.jtaf.ui.OrganizationsView;
import ch.jtaf.usecase.UseCase;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouterLink;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-003: Sign in.
 * <p>
 * The browserless submit never reaches the Spring Security filter chain; the server-side
 * authentication (credential verification, JWT cookie, redirect) is covered by
 * {@link UC003SignInAuthenticationTest}.
 * <p>
 * See {@code docs/use_cases/uc-003-sign-in.md}.
 */
class UC003SignInTest extends AbstractViewTest {

	@Autowired
	private SecurityContext securityContext;

	@Test
	@UseCase(id = "UC-003")
	void login_overlay_shows_email_and_password_fields() {
		setupVaadin();

		navigate(LoginView.class);

		LoginOverlay loginOverlay = find(LoginOverlay.class).single();
		assertThat(loginOverlay.isOpened()).isTrue();

		// The form labels are configured through the i18n element property
		String i18n = i18nProperty(loginOverlay);
		assertThat(i18n).contains("\"username\":\"Email\"")
			.contains("\"password\":\"Password\"")
			.contains("\"submit\":\"Sign in\"");
	}

	@Test
	@UseCase(id = "UC-003", scenario = "A1: Invalid credentials")
	void failed_login_stays_unauthenticated_on_the_overlay() {
		setupVaadin();

		navigate(LoginView.class);

		LoginOverlay loginOverlay = find(LoginOverlay.class).single();

		try {
			test(loginOverlay).login("not.existing@user.com", "pass");
		}
		catch (IllegalStateException _) {
			// From GoogleAnalyticsTracker. Ignore
		}

		// Post-F-1: no authentication is established
		assertThat(securityContext.isUserLoggedIn()).isFalse();
		// Post-F-2: the visitor remains on the login overlay and may retry
		assertThat(find(LoginOverlay.class).single().isOpened()).isTrue();
	}

	@Test
	@UseCase(id = "UC-003", scenario = "A1: Invalid credentials")
	void error_parameter_shows_localized_authentication_error() {
		setupVaadin();

		UI.getCurrent().navigate("login", QueryParameters.fromString("error"));

		LoginOverlay loginOverlay = find(LoginOverlay.class).single();
		assertThat(loginOverlay.isError()).isTrue();
		assertThat(i18nProperty(loginOverlay)).contains("Incorrect email or password")
			.contains("Check that you have entered the correct email and password and try again.");
		// The visitor may retry from the still open overlay
		assertThat(loginOverlay.isOpened()).isTrue();
	}

	@Test
	@UseCase(id = "UC-003", scenario = "A3: Already signed in")
	void already_logged_in() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		UI.getCurrent().navigate(LoginView.class);

		H1 h1 = find(H1.class).id("view-title");
		assertThat(h1.getText()).isEqualTo("Dashboard");
	}

	@Test
	@UseCase(id = "UC-003")
	void drawer_reveals_protected_links_after_sign_in() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		assertThat(find(RouterLink.class).id("series-list-link").isVisible()).isTrue();
		assertThat(visibleRouterLinkTargets()).contains("events", "clubs", "athletes");

		// Post-S-2: the MainLayout shows the username and the logout button
		Button logout = find(Button.class).id("logout");
		assertThat(logout.isVisible()).isTrue();
		assertThat(logout.getText()).isEqualTo("Logout (simon@martinelli.ch)");
	}

	@Test
	@UseCase(id = "UC-003")
	void drawer_hides_protected_links_for_visitors() {
		setupVaadin();

		// The queries only match visible components: the protected links are hidden
		assertThat(find(RouterLink.class).withId("series-list-link").all()).isEmpty();
		assertThat(visibleRouterLinkTargets()).doesNotContain("events", "clubs", "athletes");
		assertThat(find(Button.class).withId("logout").all()).isEmpty();
	}

	@Test
	@UseCase(id = "UC-003", businessRules = "BR-006")
	void anonymous_visitor_cannot_open_protected_views() {
		setupVaadin();

		UI.getCurrent().navigate(OrganizationsView.class);

		// The navigation is denied: the visitor never reaches the protected view
		assertThat(find(H1.class).id("view-title").getText()).isEqualTo("Dashboard");
		assertThat(securityContext.isUserLoggedIn()).isFalse();
	}

	@Test
	@UseCase(id = "UC-003", businessRules = "BR-006")
	void user_role_may_open_protected_views() {
		login("simon@martinelli.ch", "", List.of(Role.USER));

		UI.getCurrent().navigate(OrganizationsView.class);

		H1 h1 = find(H1.class).id("view-title");
		assertThat(h1.getText()).isEqualTo("Organizations");
	}

	private static String i18nProperty(LoginOverlay loginOverlay) {
		return String.valueOf(loginOverlay.getElement().getPropertyRaw("i18n"));
	}

	private List<String> visibleRouterLinkTargets() {
		return find(RouterLink.class).all()
			.stream()
			.filter(com.vaadin.flow.component.Component::isVisible)
			.map(RouterLink::getHref)
			.toList();
	}

}
