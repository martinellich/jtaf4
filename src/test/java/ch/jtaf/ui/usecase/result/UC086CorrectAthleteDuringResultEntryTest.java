package ch.jtaf.ui.usecase.result;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import ch.jtaf.ui.dialog.AthleteNameDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
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
 * UC-086: Correct athlete during result entry.
 * <p>
 * Uses the competition "1. CIS Twann" of the series "CIS 2019", which is the second
 * series on the dashboard, and the athlete "Anaïs Amos" (id 15).
 * <p>
 * See {@code docs/use_cases/uc-086-correct-athlete-during-result-entry.md}.
 */
class UC086CorrectAthleteDuringResultEntryTest extends AbstractViewTest {

	private static final long AMOS_ID = 15L;

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
		athleteDAO.findById(AMOS_ID).ifPresent(athlete -> {
			athlete.setLastName("Amos");
			athlete.setFirstName("Anaïs");
			athleteDAO.save(athlete);
		});
	}

	@Test
	void edit_is_only_possible_with_a_selected_athlete() {
		Button editAthlete = find(Button.class).id("edit-athlete");
		assertThat(editAthlete.isEnabled()).isFalse();

		// The only match is auto-selected once the grid has fetched its rows
		test(find(TextField.class).id("filter")).setValue("Amos");
		Grid<Record5<Long, String, String, String, Long>> grid = resultGrid();
		assertThat(test(grid).size()).isEqualTo(1);
		assertThat(grid.asSingleSelect().getValue()).isNotNull();
		assertThat(editAthlete.isEnabled()).isTrue();

		test(find(TextField.class).id("filter")).setValue("Nobody");
		assertThat(test(grid).size()).isZero();
		assertThat(grid.asSingleSelect().getValue()).isNull();
		assertThat(editAthlete.isEnabled()).isFalse();
	}

	@Test
	void correct_misspelled_name() {
		test(find(TextField.class).id("filter")).setValue("Amos");
		Grid<Record5<Long, String, String, String, Long>> grid = resultGrid();
		assertThat(test(grid).size()).isEqualTo(1);
		assertThat(find(TextField.class).id("result-0")).isNotNull();

		find(Button.class).id("edit-athlete").click();

		AthleteNameDialog dialog = find(AthleteNameDialog.class).single();
		assertThat(dialog.isOpened()).isTrue();
		assertThat(find(TextField.class).withCaption("Last Name").single().getValue()).isEqualTo("Amos");
		assertThat(find(TextField.class).withCaption("First Name").single().getValue()).isEqualTo("Anaïs");
		// Only the name is editable: gender, year and club would change the category
		assertThat(find(Select.class).all()).isEmpty();
		assertThat(find(TextField.class).withCaption("Year").all()).isEmpty();

		test(find(TextField.class).withCaption("Last Name").single()).setValue("Amoos");
		find(Button.class).id("edit-save").click();

		// The name is stored and the athlete stays selected for result entry
		assertThat(dialog.isOpened()).isFalse();
		assertThat(athleteDAO.findById(AMOS_ID)).get().extracting(a -> a.getLastName()).isEqualTo("Amoos");
		assertThat(find(TextField.class).id("filter").getValue()).isEqualTo(String.valueOf(AMOS_ID));
		assertThat(test(grid).size()).isEqualTo(1);
		assertThat(test(grid).getRow(0).get(ATHLETE.LAST_NAME)).isEqualTo("Amoos");
		assertThat(grid.asSingleSelect().getValue()).isNotNull();
		assertThat(grid.asSingleSelect().getValue().get(ATHLETE.ID)).isEqualTo(AMOS_ID);
		assertThat(find(TextField.class).id("result-0")).isNotNull();
	}

	@Test
	void cancel_keeps_the_name() {
		test(find(TextField.class).id("filter")).setValue("Amos");

		find(Button.class).id("edit-athlete").click();
		AthleteNameDialog dialog = find(AthleteNameDialog.class).single();

		test(find(TextField.class).withCaption("Last Name").single()).setValue("Amoos");
		find(Button.class).withCaption("Cancel").single().click();

		assertThat(dialog.isOpened()).isFalse();
		assertThat(athleteDAO.findById(AMOS_ID)).get().extracting(a -> a.getLastName()).isEqualTo("Amos");
		assertThat(test(resultGrid()).getRow(0).get(ATHLETE.LAST_NAME)).isEqualTo("Amos");
	}

	@SuppressWarnings("unchecked")
	private Grid<Record5<Long, String, String, String, Long>> resultGrid() {
		return find(Grid.class).all().getFirst();
	}

}
