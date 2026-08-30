package ch.jtaf.ui.usecase.result;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.domain.EventDAO;
import ch.jtaf.domain.ResultDAO;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import ch.jtaf.usecase.UseCase;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import org.jooq.Record5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.Result.RESULT;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-080: Enter result.
 * <p>
 * Also exercises UC-084 (Calculate IAAF points) — each entered result triggers point
 * calculation which is asserted alongside the value.
 * <p>
 * The dashboard button {@code enter-results-1-1} opens competition 6 (series
 * "Jugendmeisterschaft 2019"), {@code enter-results-2-1} competition 4 (series "CIS
 * 2019").
 * <p>
 * See {@code docs/use_cases/uc-080-enter-result.md} and
 * {@code docs/use_cases/uc-084-calculate-iaaf-points.md}.
 */
class UC080EnterResultTest extends AbstractViewTest {

	@Autowired
	private AthleteDAO athleteDAO;

	@Autowired
	private ResultDAO resultDAO;

	@Autowired
	private EventDAO eventDAO;

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);
	}

	@AfterEach
	void removeCreatedResults() {
		// The seed contains no results for Ansari in competition 6 nor for Amos in
		// competition 4; everything found there was created by these tests.
		deleteResults("Ansari", 6L);
		deleteResults("Amos", 4L);
	}

	@Test
	@UseCase(id = "UC-080")
	void check_pre_entered_results() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Martinelli");

		assertThat(find(TextField.class).withCaption("80 m").single().getValue()).isEqualTo("12.12");
		assertThat(find(TextField.class).id("points-0").getValue()).isEqualTo("402");
	}

	@Test
	@UseCase(id = "UC-080", businessRules = { "BR-047", "BR-048" })
	void enter_new_results() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Ansari");
		Record5<Long, String, String, String, Long> selected = selectedAthlete();
		long athleteId = selected.get(ATHLETE.ID);
		long categoryId = selected.get(CATEGORY.ID);

		test(find(TextField.class).id("result-0")).setValue("12.34");
		assertThat(find(TextField.class).id("points-0").getValue()).isEqualTo("48");

		test(find(TextField.class).id("result-1")).setValue("2.11");
		assertThat(find(TextField.class).id("points-1").getValue()).isEqualTo("108");

		test(find(TextField.class).id("result-2")).setValue("23.45");
		assertThat(find(TextField.class).id("points-2").getValue()).isEqualTo("252");

		// BR-047 auto-save: every value change persisted a RESULT row, BR-048: each row
		// inherits its position from the iteration index over the category's events
		var events = eventDAO.findByCategoryIdOrderByPosition(categoryId);
		String[] expectedResults = { "12.34", "2.11", "23.45" };
		int[] expectedPoints = { 48, 108, 252 };
		for (int i = 0; i < expectedResults.length; i++) {
			var resultRecord = resultDAO.getResults(6L, athleteId, categoryId, events.get(i).getId()).orElseThrow();
			assertThat(resultRecord.getResult()).isEqualTo(expectedResults[i]);
			assertThat(resultRecord.getPoints()).isEqualTo(expectedPoints[i]);
			assertThat(resultRecord.getPosition()).isEqualTo(i);
		}
	}

	@Test
	@UseCase(id = "UC-080")
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

	@Test
	@UseCase(id = "UC-080", scenario = "A1: Athlete not yet selected")
	void grid_and_form_are_empty_before_a_filter_is_typed() {
		find(Button.class).id("enter-results-1-1").click();

		// Step 2: the athletes grid is initially empty
		assertThat(test(athletesGrid()).size()).isZero();
		// A1: the form area below the grid stays empty
		assertThat(find(TextField.class).withId("result-0").all()).isEmpty();
	}

	@Test
	@UseCase(id = "UC-080")
	void manually_selecting_an_athlete_builds_the_form() {
		find(Button.class).id("enter-results-1-1").click();

		// Several athletes match, so nothing is auto-selected
		test(find(TextField.class).id("filter")).setValue("Aebi");
		Grid<Record5<Long, String, String, String, Long>> grid = athletesGrid();
		assertThat(test(grid).size()).isEqualTo(3);
		assertThat(grid.asSingleSelect().getValue()).isNull();
		assertThat(find(TextField.class).withId("result-0").all()).isEmpty();

		// Step 3: the user selects an athlete
		test(grid).select(0);
		assertThat(grid.asSingleSelect().getValue()).isNotNull();
		assertThat(find(TextField.class).withId("result-0").all()).hasSize(1);
	}

	@Test
	@UseCase(id = "UC-080", scenario = "A2: Filter resolves to zero athletes")
	void filter_without_match_clears_the_form() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Martinelli");
		assertThat(find(TextField.class).withId("result-0").all()).hasSize(1);

		test(find(TextField.class).id("filter")).setValue("Zzzzzz");

		assertThat(test(athletesGrid()).size()).isZero();
		assertThat(athletesGrid().asSingleSelect().getValue()).isNull();
		assertThat(find(TextField.class).withId("result-0").all()).isEmpty();
	}

	@Test
	@UseCase(id = "UC-080", scenario = "Failure Postconditions")
	void invalid_input_shows_notification_and_saves_nothing() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Martinelli");
		Record5<Long, String, String, String, Long> selected = selectedAthlete();
		long athleteId = selected.get(ATHLETE.ID);
		long categoryId = selected.get(CATEGORY.ID);
		long firstEventId = eventDAO.findByCategoryIdOrderByPosition(categoryId).getFirst().getId();

		test(find(TextField.class).id("result-0")).setValue("12.2.2");

		assertThat(find(Notification.class).all()).isNotEmpty();
		assertThat(test(find(Notification.class).single()).getText()).isEqualTo("Invalid result");
		// The points field and the persisted row are unchanged
		assertThat(find(TextField.class).id("points-0").getValue()).isEqualTo("402");
		var resultRecord = resultDAO.getResults(6L, athleteId, categoryId, firstEventId).orElseThrow();
		assertThat(resultRecord.getResult()).isEqualTo("12.12");
		assertThat(resultRecord.getPoints()).isEqualTo(402);
	}

	@Test
	@UseCase(id = "UC-080", businessRules = "BR-049")
	void points_fields_are_read_only() {
		find(Button.class).id("enter-results-1-1").click();

		test(find(TextField.class).id("filter")).setValue("Martinelli");

		TextField points = find(TextField.class).id("points-0");
		assertThat(points.isReadOnly()).isTrue();
		assertThat(points.isEnabled()).isFalse();
	}

	@SuppressWarnings("unchecked")
	private Grid<Record5<Long, String, String, String, Long>> athletesGrid() {
		return find(Grid.class).all().getFirst();
	}

	private Record5<Long, String, String, String, Long> selectedAthlete() {
		Record5<Long, String, String, String, Long> selected = athletesGrid().asSingleSelect().getValue();
		assertThat(selected).isNotNull();
		return selected;
	}

	private void deleteResults(String lastName, long competitionId) {
		athleteDAO.findAll(ATHLETE.LAST_NAME.eq(lastName))
			.forEach(athlete -> resultDAO
				.delete(RESULT.ATHLETE_ID.eq(athlete.getId()).and(RESULT.COMPETITION_ID.eq(competitionId))));
	}

}
