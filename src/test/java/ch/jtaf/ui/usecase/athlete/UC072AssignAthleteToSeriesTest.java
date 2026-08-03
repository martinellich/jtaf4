package ch.jtaf.ui.usecase.athlete;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.SearchAthleteDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-072: Assign athlete to series.
 * <p>
 * Also exercises UC-073 (Remove athlete from series) — the test assigns and then removes
 * the athlete.
 * <p>
 * See {@code docs/use_cases/uc-072-assign-athlete-to-series.md} and
 * {@code docs/use_cases/uc-073-remove-athlete-from-series.md}.
 */
class UC072AssignAthleteToSeriesTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		TextField name = find(TextField.class).single();
		assertThat(name.getValue()).isEqualTo("CIS 2019");
	}

	@Test
	void assign_athelete() {
		Tabs tabs = find(Tabs.class).single();
		Tab athletes = find(Tab.class).withText("Athletes").single();
		tabs.setSelectedTab(athletes);

		// Check content of athletes grid
		Grid<AthleteRecord> athletesGrid = find(Grid.class).id("athletes-grid");
		assertThat(test(athletesGrid).size()).isEqualTo(108);
		assertThat(test(athletesGrid).getRow(0).getLastName()).isEqualTo("Berger");

		// Assign athlete
		gridHeaderButton(athletesGrid, "remove-column").click();

		assertThat(find(SearchAthleteDialog.class).all()).hasSize(1);

		// Test maximize and restore
		Button toggle = find(Button.class).id("toggle");
		toggle.click();
		toggle.click();

		test(find(TextField.class).withCaption("Filter").withValue("").single()).setValue("z");

		Grid<AthleteRecord> searchAthletesGrid = find(Grid.class).id("search-athletes-grid");
		assertThat(test(searchAthletesGrid).size()).isEqualTo(1);

		assertThat(test(searchAthletesGrid).getRow(0).getLastName()).isEqualTo("Zimmermann");

		test(searchAthletesGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		// Check if athlete was assigned
		assertThat(test(athletesGrid).size()).isEqualTo(109);
		int assignedRow = findRowByLastName(athletesGrid, "Zimmermann");

		// Remove the assigned athlete again so the seeded state is restored for other
		// tests.
		test(athletesGrid).getCellComponent(assignedRow, "remove-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = find(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		find(Button.class).id("athlete-delete-confirm-dialog-confirm").click();

		// Check if athlete was removed
		assertThat(test(athletesGrid).size()).isEqualTo(108);
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
