package ch.jtaf.ui.usecase.result;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-081: Mark athlete DNF.
 * <p>
 * See {@code docs/use_cases/uc-081-mark-athlete-dnf.md}.
 */
class UC081MarkAthleteDnfTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	void toggle_dnf() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Martinelli");

		Checkbox dnf = find(Checkbox.class).withCaption("DNF").single();
		assertThat(dnf.getValue()).isFalse();

		test(dnf).click();
		assertThat(dnf.getValue()).isTrue();

		// Reset to keep other tests deterministic
		test(dnf).click();
		assertThat(dnf.getValue()).isFalse();
	}

}
