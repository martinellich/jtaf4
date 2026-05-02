package ch.jtaf.ui.usecase.report;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-090: View dashboard.
 * <p>
 * See {@code docs/use_cases/uc-090-view-dashboard.md}.
 */
@SuppressWarnings("java:S2699")
class UC090ViewDashboardTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	void title_is_dashboard() {
		H1 h1 = $(H1.class).id("view-title");
		assertThat(h1.getText()).isEqualTo("Dashboard");
	}

	@Test
	void series_are_displayed() {
		assertThat($(HorizontalLayout.class).withClassName("series-layout").all()).hasSize(4);
	}

	@Test
	void enter_results_is_enabled() {
		assertThat($(Button.class).withText("Enter Results").all()).hasSize(6);

		try {
			$(Button.class).id("logout").click();
		}
		catch (Exception ignored) {
			// https://github.com/mvysny/karibu-testing/issues/148
		}
	}

}
