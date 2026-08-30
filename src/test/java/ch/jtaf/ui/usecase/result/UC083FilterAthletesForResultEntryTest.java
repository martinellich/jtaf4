package ch.jtaf.ui.usecase.result;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import ch.jtaf.usecase.UseCase;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.jooq.Record5;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-083: Filter athletes for result entry.
 * <p>
 * The dashboard button {@code enter-results-1-1} opens competition 6 (series
 * "Jugendmeisterschaft 2019"). The seeded athlete Lukas Martinelli (id 140) is enrolled
 * there; "Aebi" matches three athletes.
 * <p>
 * The browser focus of the first result field (BR-053) cannot be observed server-side; it
 * needs a browser-based (Playwright) test.
 * <p>
 * See {@code docs/use_cases/uc-083-filter-athletes-for-result-entry.md}.
 */
class UC083FilterAthletesForResultEntryTest extends AbstractViewTest {

	@Autowired
	private AthleteDAO athleteDAO;

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@Test
	@UseCase(id = "UC-083", businessRules = "BR-052")
	void search_with_id() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("140");

		assertThat(test(athletesGrid()).size()).isEqualTo(1);
		assertThat(find(TextField.class).withCaption("80 m").single().getValue()).isEqualTo("12.12");
		assertThat(find(TextField.class).id("points-0").getValue()).isEqualTo("402");
	}

	@Test
	@UseCase(id = "UC-083")
	void filter_field_is_focused_and_reacts_to_every_keystroke() {
		find(Button.class).id("enter-results-1-1").click();

		TextField filter = find(TextField.class).id("filter");
		// Step 1: the filter field receives the focus via autofocus
		assertThat(filter.isAutofocus()).isTrue();
		// Step 3: the data provider is recomputed on every keystroke
		assertThat(filter.getValueChangeMode()).isEqualTo(ValueChangeMode.EAGER);
	}

	@Test
	@UseCase(id = "UC-083")
	void name_filter_matches_first_or_last_name_case_insensitively() {
		find(Button.class).id("enter-results-1-1").click();

		// Step 5: case-insensitive prefix on the first name
		test(find(TextField.class).id("filter")).setValue("luka");
		Grid<Record5<Long, String, String, String, Long>> grid = athletesGrid();
		assertThat(test(grid).size()).isEqualTo(1);
		assertThat(test(grid).getRow(0).get(ATHLETE.LAST_NAME)).isEqualTo("Martinelli");

		// and on the last name
		test(find(TextField.class).id("filter")).setValue("aebi");
		assertThat(test(grid).size()).isEqualTo(3);
	}

	@Test
	@UseCase(id = "UC-083", businessRules = "BR-053")
	void single_match_auto_selects_the_athlete_and_opens_the_form() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("140");

		Grid<Record5<Long, String, String, String, Long>> grid = athletesGrid();
		assertThat(test(grid).size()).isEqualTo(1);
		assertThat(grid.asSingleSelect().getValue()).isNotNull();
		assertThat(find(TextField.class).withId("result-0").all()).hasSize(1);
	}

	@Test
	@UseCase(id = "UC-083", businessRules = "BR-053")
	void multiple_matches_do_not_auto_select() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Aebi");

		Grid<Record5<Long, String, String, String, Long>> grid = athletesGrid();
		assertThat(test(grid).size()).isEqualTo(3);
		assertThat(grid.asSingleSelect().getValue()).isNull();
		assertThat(find(TextField.class).withId("result-0").all()).isEmpty();
	}

	@Test
	@UseCase(id = "UC-083", businessRules = "BR-052")
	void unknown_number_never_falls_back_to_name_search() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("888888");

		assertThat(test(athletesGrid()).size()).isZero();
	}

	@Test
	@UseCase(id = "UC-083", scenario = "A1: Empty filter")
	void clearing_the_filter_lists_all_athletes_and_keeps_the_form() {
		find(Button.class).id("enter-results-1-1").click();

		// The grid is empty before any filter has been typed
		Grid<Record5<Long, String, String, String, Long>> grid = athletesGrid();
		assertThat(test(grid).size()).isZero();

		test(find(TextField.class).id("filter")).setValue("140");
		assertThat(find(TextField.class).withId("result-0").all()).hasSize(1);

		// Clearing the filter back to "" lists all athletes of the competition
		test(find(TextField.class).id("filter")).setValue("");
		assertThat(test(grid).size()).isEqualTo(athleteDAO.countAthletes(6L, DSL.noCondition()));

		// and the form below the grid is not cleared
		assertThat(grid.asSingleSelect().getValue()).isNotNull();
		assertThat(find(TextField.class).withId("result-0").all()).hasSize(1);
	}

	@SuppressWarnings("unchecked")
	private Grid<Record5<Long, String, String, String, Long>> athletesGrid() {
		return find(Grid.class).all().getFirst();
	}

}
