package ch.jtaf.ui.usecase.competition;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.dialog.CompetitionDialog;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-032: Delete competition.
 * <p>
 * See {@code docs/use_cases/uc-032-delete-competition.md}.
 */
class UC032DeleteCompetitionTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		assertThat($(TextField.class).single().getValue()).isEqualTo("CIS 2019");
	}

	@Test
	void delete_competition() {
		// Add a competition that has no results (BR-029 prevents deleting seeded ones).
		Grid<CompetitionRecord> competitionsGrid = $(Grid.class).id("competitions-grid");
		assertThat(test(competitionsGrid).size()).isEqualTo(2);

		gridHeaderButton(competitionsGrid, "edit-column").click();
		assertThat($(CompetitionDialog.class).all()).hasSize(1);

		test($(TextField.class).withCaption("Name").withValue("").single()).setValue("Disposable");
		test($(DatePicker.class).withCaption("Date").single()).setValue(LocalDate.now());
		$(Button.class).id("edit-save").click();

		assertThat(test(competitionsGrid).size()).isEqualTo(3);
		int deleteRow = 2;
		assertThat(test(competitionsGrid).getRow(deleteRow).getName()).isEqualTo("Disposable");

		// User cancels — dialog closes and the competition stays.
		clickDeleteOnRow(competitionsGrid, deleteRow);
		ConfirmDialog confirmDialog = $(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id("delete-confirm-dialog-cancel").click();

		assertThat($(ConfirmDialog.class).all()).isEmpty();
		assertThat(test(competitionsGrid).size()).isEqualTo(3);

		// User confirms — competition row is removed.
		clickDeleteOnRow(competitionsGrid, deleteRow);
		assertThat($(ConfirmDialog.class).single().isOpened()).isTrue();
		$(Button.class).id("delete-confirm-dialog-confirm").click();

		assertThat(test(competitionsGrid).size()).isEqualTo(2);
	}

	private void clickDeleteOnRow(Grid<CompetitionRecord> competitionsGrid, int row) {
		test(competitionsGrid).getCellComponent(row, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);
	}

}
