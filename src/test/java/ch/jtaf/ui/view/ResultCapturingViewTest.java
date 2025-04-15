package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.ui.KaribuTest;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class ResultCapturingViewTest extends KaribuTest {

	@BeforeEach
	public void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		UI.getCurrent().getPage().reload();
	}

	@Test
	void check_pre_entered_results() {
		_get(Button.class, spec -> spec.withId("enter-results-1-1")).click();

		_get(TextField.class, spec -> spec.withId("filter")).setValue("Martinelli");

		assertThat(_get(TextField.class, spec1 -> spec1.withLabel("80 m")).getValue()).isEqualTo("12.12");
		assertThat(_get(TextField.class, spec -> spec.withId("points-0")).getValue()).isEqualTo("402");
	}

	@Test
	void enter_new_results() {
		_get(Button.class, spec -> spec.withId("enter-results-1-1")).click();

		_get(TextField.class, spec -> spec.withId("filter")).setValue("Ansari");

		_get(TextField.class, spec -> spec.withId("result-0")).setValue("12.34");
		assertThat(_get(TextField.class, spec2 -> spec2.withId("points-0")).getValue()).isEqualTo("48");

		_get(TextField.class, spec -> spec.withId("result-1")).setValue("2.11");
		assertThat(_get(TextField.class, spec1 -> spec1.withId("points-1")).getValue()).isEqualTo("108");

		_get(TextField.class, spec -> spec.withId("result-2")).setValue("23.45");
		assertThat(_get(TextField.class, spec -> spec.withId("points-2")).getValue()).isEqualTo("252");
	}

	@Test
	void search_with_id() {
		_get(Button.class, spec -> spec.withId("enter-results-1-1")).click();

		_get(TextField.class, spec -> spec.withId("filter")).setValue("140");

		assertThat(_get(TextField.class, spec1 -> spec1.withLabel("80 m")).getValue()).isEqualTo("12.12");
		assertThat(_get(TextField.class, spec -> spec.withId("points-0")).getValue()).isEqualTo("402");
	}

	@Test
	void enter_new_results_incl_long_run() {
		_get(Button.class, spec -> spec.withId("enter-results-2-1")).click();

		_get(TextField.class, spec -> spec.withId("filter")).setValue("Amos");

		_get(TextField.class, spec -> spec.withId("result-0")).setValue("12.34");
		assertThat(_get(TextField.class, spec2 -> spec2.withId("points-0")).getValue()).isEqualTo("453");

		_get(TextField.class, spec -> spec.withId("result-1")).setValue("2.11");
		assertThat(_get(TextField.class, spec1 -> spec1.withId("points-1")).getValue()).isEqualTo("146");

		_get(TextField.class, spec -> spec.withId("result-2")).setValue("23.45");
		assertThat(_get(TextField.class, spec -> spec.withId("points-2")).getValue()).isEqualTo("340");

		_get(TextField.class, spec -> spec.withId("result-3")).setValue("2.52");
		assertThat(_get(TextField.class, spec -> spec.withId("points-3")).getValue()).isEqualTo("68");
	}

}
