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
 * UC-073: Remove athlete from series.
 * <p>
 * See {@code docs/use_cases/uc-073-remove-athlete-from-series.md}.
 */
class UC073RemoveAthleteFromSeriesTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		assertThat($(TextField.class).single().getValue()).isEqualTo("CIS 2019");
	}

	@Test
	void remove_athlete_from_series() {
		Tabs tabs = $(Tabs.class).single();
		tabs.setSelectedTab($(Tab.class).withText("Athletes").single());

		Grid<AthleteRecord> athletesGrid = $(Grid.class).id("athletes-grid");
		int baseline = test(athletesGrid).size();

		// Assign an unassigned athlete so the test can remove it without altering seeded
		// state.
		gridHeaderButton(athletesGrid, "remove-column").click();
		assertThat($(SearchAthleteDialog.class).all()).hasSize(1);

		test($(TextField.class).withCaption("Filter").withValue("").single()).setValue("z");
		Grid<AthleteRecord> searchAthletesGrid = $(Grid.class).id("search-athletes-grid");
		assertThat(test(searchAthletesGrid).size()).isEqualTo(1);
		assertThat(test(searchAthletesGrid).getRow(0).getLastName()).isEqualTo("Zimmermann");

		test(searchAthletesGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		assertThat(test(athletesGrid).size()).isEqualTo(baseline + 1);
		int newRow = findRowByLastName(athletesGrid, "Zimmermann");
		Long newAthleteId = test(athletesGrid).getRow(newRow).getId();

		// A1: user cancels — the athlete stays in the series.
		clickRemoveOnRow(athletesGrid, newRow);
		ConfirmDialog confirmDialog = $(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id("athlete-delete-confirm-dialog-cancel").click();

		assertThat($(ConfirmDialog.class).all()).isEmpty();
		assertThat(test(athletesGrid).size()).isEqualTo(baseline + 1);
		assertThat(test(athletesGrid).getRow(newRow).getId()).isEqualTo(newAthleteId);

		// Main flow: user confirms — the CATEGORY_ATHLETE rows are deleted.
		clickRemoveOnRow(athletesGrid, newRow);
		assertThat($(ConfirmDialog.class).single().isOpened()).isTrue();
		$(Button.class).id("athlete-delete-confirm-dialog-confirm").click();

		assertThat(test(athletesGrid).size()).isEqualTo(baseline);
		for (int i = 0; i < baseline; i++) {
			assertThat(test(athletesGrid).getRow(i).getId()).isNotEqualTo(newAthleteId);
		}
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

	private void clickRemoveOnRow(Grid<AthleteRecord> athletesGrid, int row) {
		test(athletesGrid).getCellComponent(row, "remove-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);
	}

}
