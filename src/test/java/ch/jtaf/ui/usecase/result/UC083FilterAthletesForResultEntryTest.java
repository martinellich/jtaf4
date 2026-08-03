package ch.jtaf.ui.usecase.result;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-083: Filter athletes for result entry.
 * <p>
 * See {@code docs/use_cases/uc-083-filter-athletes-for-result-entry.md}.
 */
class UC083FilterAthletesForResultEntryTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	void search_with_id() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("140");

		assertThat(find(TextField.class).withCaption("80 m").single().getValue()).isEqualTo("12.12");
		assertThat(find(TextField.class).id("points-0").getValue()).isEqualTo("402");
	}

}
