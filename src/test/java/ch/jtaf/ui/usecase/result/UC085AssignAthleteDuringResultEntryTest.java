package ch.jtaf.ui.usecase.result;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.domain.CategoryAthleteDAO;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import ch.jtaf.ui.dialog.AthleteDialog;
import ch.jtaf.ui.dialog.SearchAthleteDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.jooq.Record5;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-085: Assign athlete during result entry.
 * <p>
 * Uses the competition "1. CIS Twann" of the series "CIS 2019" (series id 3), which is
 * the second series on the dashboard.
 * <p>
 * See {@code docs/use_cases/uc-085-assign-athlete-during-result-entry.md}.
 */
class UC085AssignAthleteDuringResultEntryTest extends AbstractViewTest {

	private static final long CIS_2019_SERIES_ID = 3L;

	private static final long ZIMMERMANN_ID = 1L;

	@Autowired
	private CategoryAthleteDAO categoryAthleteDAO;

	@Autowired
	private AthleteDAO athleteDAO;

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		// Deliberately no active organization: the view derives it from the series
		navigate(DashboardView.class);
		find(Button.class).id("enter-results-2-1").click();
	}

	@AfterEach
	void cleanUp() {
		// Restore the seeded state for the other tests
		athleteDAO.findById(ZIMMERMANN_ID)
			.ifPresent(athlete -> categoryAthleteDAO.deleteCategoryAthlete(athlete, CIS_2019_SERIES_ID));
		athleteDAO.findAll(ATHLETE.LAST_NAME.eq("Neu")).forEach(athlete -> {
			categoryAthleteDAO.deleteCategoryAthlete(athlete, CIS_2019_SERIES_ID);
			athleteDAO.delete(athlete);
		});
	}

	@Test
	void assign_existing_athlete() {
		// Zimmermann exists in the organization but is not enrolled in CIS 2019
		test(find(TextField.class).id("filter")).setValue("Zimmer");
		Grid<Record5<Long, String, String, String, Long>> grid = resultGrid();
		assertThat(test(grid).size()).isZero();

		find(Button.class).id("assign-athlete").click();

		SearchAthleteDialog dialog = find(SearchAthleteDialog.class).single();
		assertThat(dialog.isOpened()).isTrue();
		assertThat(find(TextField.class).withCaption("Filter").single().getValue()).isEqualTo("Zimmer");

		Grid<AthleteRecord> searchAthletesGrid = find(Grid.class).id("search-athletes-grid");
		assertThat(test(searchAthletesGrid).size()).isEqualTo(1);
		assertThat(test(searchAthletesGrid).getRow(0).getLastName()).isEqualTo("Zimmermann");

		test(searchAthletesGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		// The athlete is enrolled, selected and the result form is shown
		assertThat(dialog.isOpened()).isFalse();
		assertThat(categoryAthleteDAO.isAssignedToSeries(ZIMMERMANN_ID, CIS_2019_SERIES_ID)).isTrue();
		assertThat(find(TextField.class).id("filter").getValue()).isEqualTo(String.valueOf(ZIMMERMANN_ID));
		assertThat(test(grid).size()).isEqualTo(1);
		assertThat(test(grid).getRow(0).get(ATHLETE.LAST_NAME)).isEqualTo("Zimmermann");
		assertThat(grid.asSingleSelect().getValue()).isNotNull();
		assertThat(find(TextField.class).id("result-0")).isNotNull();
	}

	@Test
	void create_and_assign_new_athlete() {
		test(find(TextField.class).id("filter")).setValue("Neu");
		Grid<Record5<Long, String, String, String, Long>> grid = resultGrid();
		assertThat(test(grid).size()).isZero();

		find(Button.class).id("assign-athlete").click();
		SearchAthleteDialog dialog = find(SearchAthleteDialog.class).single();

		Grid<AthleteRecord> searchAthletesGrid = find(Grid.class).id("search-athletes-grid");
		assertThat(test(searchAthletesGrid).size()).isZero();

		gridHeaderButton(searchAthletesGrid, "edit-column").click();
		assertThat(find(AthleteDialog.class).all()).hasSize(1);

		test(find(TextField.class).withCaption("Last Name").single()).setValue("Neu");
		test(find(TextField.class).withCaption("First Name").single()).setValue("Nina");
		test(find(Select.class).withCaption("Gender").single()).selectItem("F");
		test(find(TextField.class).withCaption("Year").single()).setValue("2011");
		find(Button.class).id("edit-save").click();

		// The new athlete is enrolled without a further click and selected for result
		// entry
		assertThat(dialog.isOpened()).isFalse();
		List<AthleteRecord> created = athleteDAO.findAll(ATHLETE.LAST_NAME.eq("Neu"));
		assertThat(created).hasSize(1);
		assertThat(categoryAthleteDAO.isAssignedToSeries(created.getFirst().getId(), CIS_2019_SERIES_ID)).isTrue();
		assertThat(test(grid).size()).isEqualTo(1);
		assertThat(test(grid).getRow(0).get(ATHLETE.LAST_NAME)).isEqualTo("Neu");
		assertThat(grid.asSingleSelect().getValue()).isNotNull();
		assertThat(find(TextField.class).id("result-0")).isNotNull();
	}

	@Test
	void no_matching_category() {
		find(Button.class).id("assign-athlete").click();

		Grid<AthleteRecord> searchAthletesGrid = find(Grid.class).id("search-athletes-grid");
		gridHeaderButton(searchAthletesGrid, "edit-column").click();

		// CIS 2019 has no category for a year of birth before 1900
		test(find(TextField.class).withCaption("Last Name").single()).setValue("Neu");
		test(find(TextField.class).withCaption("First Name").single()).setValue("Nora");
		test(find(Select.class).withCaption("Gender").single()).selectItem("F");
		test(find(TextField.class).withCaption("Year").single()).setValue("1899");
		find(Button.class).id("edit-save").click();

		List<AthleteRecord> created = athleteDAO.findAll(ATHLETE.LAST_NAME.eq("Neu"));
		assertThat(created).hasSize(1);
		assertThat(categoryAthleteDAO.isAssignedToSeries(created.getFirst().getId(), CIS_2019_SERIES_ID)).isFalse();
		assertThat(find(Notification.class).all()).isNotEmpty();
		assertThat(test(resultGrid()).size()).isZero();
	}

	@SuppressWarnings("unchecked")
	private Grid<Record5<Long, String, String, String, Long>> resultGrid() {
		return find(Grid.class).all().getFirst();
	}

}
