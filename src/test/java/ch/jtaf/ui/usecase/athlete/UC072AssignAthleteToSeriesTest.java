package ch.jtaf.ui.usecase.athlete;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.domain.CategoryAthleteDAO;
import ch.jtaf.domain.CategoryAthleteId;
import ch.jtaf.domain.CategoryDAO;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.dialog.AthleteDialog;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.SearchAthleteDialog;
import ch.jtaf.usecase.UseCase;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import org.jooq.exception.TooManyRowsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * UC-072: Assign athlete to series.
 * <p>
 * Also exercises UC-073 (Remove athlete from series) — the main scenario assigns and then
 * removes the athlete.
 * <p>
 * The series opened by {@link #login()} is "CIS 2019" (series id 3) with 108 enrolled
 * athletes; the seeded athlete Zimmermann (id 1, F, 2011) is not enrolled in it and
 * matches its category "L".
 * <p>
 * See {@code docs/use_cases/uc-072-assign-athlete-to-series.md} and
 * {@code docs/use_cases/uc-073-remove-athlete-from-series.md}.
 */
class UC072AssignAthleteToSeriesTest extends AbstractViewTest {

	private static final long CIS_2019_SERIES_ID = 3L;

	private static final long ZIMMERMANN_ID = 1L;

	@Autowired
	private AthleteDAO athleteDAO;

	@Autowired
	private CategoryDAO categoryDAO;

	@Autowired
	private CategoryAthleteDAO categoryAthleteDAO;

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		TextField name = find(TextField.class).single();
		assertThat(name.getValue()).isEqualTo("CIS 2019");
	}

	@AfterEach
	void restoreSeededState() {
		athleteDAO.findById(ZIMMERMANN_ID)
			.ifPresent(athlete -> categoryAthleteDAO.deleteCategoryAthlete(athlete, CIS_2019_SERIES_ID));
		athleteDAO.findAll(ATHLETE.LAST_NAME.eq("Fixturenomatch")).forEach(athlete -> {
			categoryAthleteDAO.deleteCategoryAthlete(athlete, CIS_2019_SERIES_ID);
			athleteDAO.delete(athlete);
		});
	}

	@Test
	@UseCase(id = "UC-072", businessRules = "BR-044")
	void assign_athlete() {
		Grid<AthleteRecord> athletesGrid = openAthletesTab();
		assertThat(test(athletesGrid).size()).isEqualTo(108);
		assertThat(test(athletesGrid).getRow(0).getLastName()).isEqualTo("Berger");

		// Step 1: open the search dialog from the grid header
		gridHeaderButton(athletesGrid, "remove-column", "assign-athlete").click();

		SearchAthleteDialog dialog = find(SearchAthleteDialog.class).single();
		assertThat(dialog.isOpened()).isTrue();

		// Test maximize and restore
		Button toggle = find(Button.class).id("toggle");
		toggle.click();
		toggle.click();

		// Step 2: the search grid is empty until a filter is typed
		Grid<AthleteRecord> searchAthletesGrid = find(Grid.class).id("search-athletes-grid");
		assertThat(test(searchAthletesGrid).size()).isZero();

		test(find(TextField.class).withCaption("Filter").withValue("").single()).setValue("z");
		assertThat(test(searchAthletesGrid).size()).isEqualTo(1);
		assertThat(test(searchAthletesGrid).getRow(0).getLastName()).isEqualTo("Zimmermann");

		// Step 3: assign via the per-row button
		test(searchAthletesGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		// Step 6: the dialog is closed and the athletes grid refreshed
		assertThat(dialog.isOpened()).isFalse();
		assertThat(test(athletesGrid).size()).isEqualTo(109);
		int assignedRow = findRowByLastName(athletesGrid, "Zimmermann");

		// Steps 4/5 and BR-044: the category was resolved by gender and birth year and
		// the CATEGORY_ATHLETE row was stored with dnf defaulting to false
		Long categoryId = categoryDAO.findIdBySeriesIdAndGenderAndYearOfBirth(CIS_2019_SERIES_ID, "F", 2011)
			.orElseThrow();
		var categoryAthlete = categoryAthleteDAO.findById(new CategoryAthleteId(ZIMMERMANN_ID, categoryId))
			.orElseThrow();
		assertThat(categoryAthlete.getDnf()).isFalse();
		assertThat(categoryAthleteDAO.countAthletesBySeriesId(CIS_2019_SERIES_ID)).isEqualTo(109);

		// UC-073: remove the assigned athlete again so the seeded state is restored
		test(athletesGrid).getCellComponent(assignedRow, "remove-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = find(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		find(Button.class).id("athlete-delete-confirm-dialog-confirm").click();

		assertThat(test(athletesGrid).size()).isEqualTo(108);
		assertThat(categoryAthleteDAO.isAssignedToSeries(ZIMMERMANN_ID, CIS_2019_SERIES_ID)).isFalse();
	}

	@Test
	@UseCase(id = "UC-072", scenario = "A1: No matching category")
	void athlete_without_matching_category_is_not_assigned() {
		Grid<AthleteRecord> athletesGrid = openAthletesTab();
		gridHeaderButton(athletesGrid, "remove-column", "assign-athlete").click();

		// A4: create a new athlete from the dialog — CIS 2019 has no category for a
		// birth year before 1900, so the assignment must fail
		Grid<AthleteRecord> searchAthletesGrid = find(Grid.class).id("search-athletes-grid");
		gridHeaderButton(searchAthletesGrid, "edit-column").click();
		assertThat(find(AthleteDialog.class).all()).hasSize(1);

		test(find(TextField.class).withCaption("Last Name").single()).setValue("Fixturenomatch");
		test(find(TextField.class).withCaption("First Name").single()).setValue("Nora");
		test(find(Select.class).withCaption("Gender").single()).selectItem("F");
		test(find(TextField.class).withCaption("Year").single()).setValue("1899");
		find(Button.class).id("edit-save").click();

		// A1: notification, and Post-F-1: no enrolment is created
		assertThat(find(Notification.class).all()).isNotEmpty();
		List<AthleteRecord> created = athleteDAO.findAll(ATHLETE.LAST_NAME.eq("Fixturenomatch"));
		assertThat(created).hasSize(1);
		assertThat(categoryAthleteDAO.isAssignedToSeries(created.getFirst().getId(), CIS_2019_SERIES_ID)).isFalse();
		assertThat(test(athletesGrid).size()).isEqualTo(108);
	}

	@Test
	@UseCase(id = "UC-072", scenario = "A2: Athlete already enrolled")
	void already_enrolled_athlete_is_excluded_from_the_search() {
		Grid<AthleteRecord> athletesGrid = openAthletesTab();
		Long enrolledAthleteId = test(athletesGrid).getRow(0).getId();

		gridHeaderButton(athletesGrid, "remove-column", "assign-athlete").click();
		Grid<AthleteRecord> searchAthletesGrid = find(Grid.class).id("search-athletes-grid");

		// The enrolled athlete does not appear in the search results
		test(find(TextField.class).withCaption("Filter").withValue("").single())
			.setValue(String.valueOf(enrolledAthleteId));
		assertThat(test(searchAthletesGrid).size()).isZero();

		// while a not yet enrolled athlete does
		test(find(TextField.class).withCaption("Filter").single()).setValue(String.valueOf(ZIMMERMANN_ID));
		assertThat(test(searchAthletesGrid).size()).isEqualTo(1);
	}

	@Test
	@UseCase(id = "UC-072", scenario = "A3: Overlapping categories")
	void overlapping_categories_fail_the_assignment() {
		// Series 8 (seed fixture) has two categories covering the same gender and years
		AthleteRecord athlete = athleteDAO.findAll(ATHLETE.LAST_NAME.eq("Fixtureoverlap")).getFirst();

		assertThatExceptionOfType(TooManyRowsException.class)
			.isThrownBy(() -> categoryAthleteDAO.createCategoryAthlete(athlete, 8L));
		assertThat(categoryAthleteDAO.isAssignedToSeries(athlete.getId(), 8L)).isFalse();
	}

	@Test
	@UseCase(id = "UC-072", businessRules = "BR-045")
	void athlete_is_enrolled_in_at_most_one_category_per_series() {
		Grid<AthleteRecord> athletesGrid = openAthletesTab();
		AthleteRecord enrolledAthlete = test(athletesGrid).getRow(0);
		assertThat(categoryAthleteDAO.isAssignedToSeries(enrolledAthlete.getId(), CIS_2019_SERIES_ID)).isTrue();
		int countBefore = categoryAthleteDAO.countAthletesBySeriesId(CIS_2019_SERIES_ID);

		// A second enrolment attempt inserts nothing
		categoryAthleteDAO.createCategoryAthlete(enrolledAthlete, CIS_2019_SERIES_ID);

		assertThat(categoryAthleteDAO.countAthletesBySeriesId(CIS_2019_SERIES_ID)).isEqualTo(countBefore);
	}

	private Grid<AthleteRecord> openAthletesTab() {
		Tabs tabs = find(Tabs.class).single();
		Tab athletes = find(Tab.class).withText("Athletes").single();
		tabs.setSelectedTab(athletes);

		return find(Grid.class).id("athletes-grid");
	}

	private int findRowByLastName(Grid<AthleteRecord> athletesGrid, String lastName) {
		int size = test(athletesGrid).size();
		for (int i = 0; i < size; i++) {
			if (lastName.equals(test(athletesGrid).getRow(i).getLastName())) {
				return i;
			}
		}
		throw new AssertionError("Athlete with last name '" + lastName + "' not found");
	}

}
