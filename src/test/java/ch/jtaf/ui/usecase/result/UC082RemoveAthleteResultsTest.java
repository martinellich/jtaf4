package ch.jtaf.ui.usecase.result;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-082: Remove athlete results.
 * <p>
 * See {@code docs/use_cases/uc-082-remove-athlete-results.md}.
 */
class UC082RemoveAthleteResultsTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	void remove_results() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Martinelli");

		// Pre-existing result for Martinelli/80 m is "12.12" (see UC-080).
		assertThat(find(TextField.class).withCaption("80 m").single().getValue()).isEqualTo("12.12");

		find(Button.class).withText("Remove results").single().click();

		ConfirmDialog confirmDialog = find(ConfirmDialog.class).id("remove-results");
		assertThat(confirmDialog.isOpened()).isTrue();
		find(Button.class).id("remove-results-confirm").click();

		// Form is rebuilt with cleared values
		assertThat(find(TextField.class).withCaption("80 m").single().getValue()).isEmpty();
		assertThat(find(TextField.class).id("points-0").getValue()).isEmpty();

		// Restore original value to keep other tests deterministic
		test(find(TextField.class).id("result-0")).setValue("12.12");
	}

}
