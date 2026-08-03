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
 * UC-080: Enter result.
 * <p>
 * Also exercises UC-084 (Calculate IAAF points) — each entered result triggers point
 * calculation which is asserted alongside the value.
 * <p>
 * See {@code docs/use_cases/uc-080-enter-result.md} and
 * {@code docs/use_cases/uc-084-calculate-iaaf-points.md}.
 */
class UC080EnterResultTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	void check_pre_entered_results() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Martinelli");

		assertThat(find(TextField.class).withCaption("80 m").single().getValue()).isEqualTo("12.12");
		assertThat(find(TextField.class).id("points-0").getValue()).isEqualTo("402");
	}

	@Test
	void enter_new_results() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Ansari");

		test(find(TextField.class).id("result-0")).setValue("12.34");
		assertThat(find(TextField.class).id("points-0").getValue()).isEqualTo("48");

		test(find(TextField.class).id("result-1")).setValue("2.11");
		assertThat(find(TextField.class).id("points-1").getValue()).isEqualTo("108");

		test(find(TextField.class).id("result-2")).setValue("23.45");
		assertThat(find(TextField.class).id("points-2").getValue()).isEqualTo("252");
	}

	@Test
	void enter_new_results_incl_long_run() {
		find(Button.class).id("enter-results-2-1").click();

		test(find(TextField.class).id("filter")).setValue("Amos");

		test(find(TextField.class).id("result-0")).setValue("12.34");
		assertThat(find(TextField.class).id("points-0").getValue()).isEqualTo("453");

		test(find(TextField.class).id("result-1")).setValue("2.11");
		assertThat(find(TextField.class).id("points-1").getValue()).isEqualTo("146");

		test(find(TextField.class).id("result-2")).setValue("23.45");
		assertThat(find(TextField.class).id("points-2").getValue()).isEqualTo("340");

		test(find(TextField.class).id("result-3")).setValue("2.52");
		assertThat(find(TextField.class).id("points-3").getValue()).isEqualTo("68");
	}

}
