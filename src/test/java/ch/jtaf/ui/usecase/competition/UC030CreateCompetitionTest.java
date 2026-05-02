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
 * UC-030: Create competition.
 * <p>
 * Also exercises UC-032 (Delete competition) — the test creates and then removes the
 * competition.
 * <p>
 * See {@code docs/use_cases/uc-030-create-competition.md} and
 * {@code docs/use_cases/uc-032-delete-competition.md}.
 */
class UC030CreateCompetitionTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		TextField name = $(TextField.class).single();
		assertThat(name.getValue()).isEqualTo("CIS 2019");
	}

	@Test
	void add_competition() {
		// Check content of competitions grid
		Grid<CompetitionRecord> competitionsGrid = $(Grid.class).id("competitions-grid");
		assertThat(test(competitionsGrid).size()).isEqualTo(2);
		assertThat(test(competitionsGrid).getRow(0).getName()).isEqualTo("1. CIS Twann");

		// Add competition
		gridHeaderButton(competitionsGrid, "edit-column").click();
		assertThat($(CompetitionDialog.class).all()).hasSize(1);

		test($(TextField.class).withCaption("Name").withValue("").single()).setValue("Test");
		test($(DatePicker.class).withCaption("Date").single()).setValue(LocalDate.now());
		$(Button.class).id("edit-save").click();

		// Check if competition was added
		assertThat(test(competitionsGrid).size()).isEqualTo(3);
		assertThat(test(competitionsGrid).getRow(2).getName()).isEqualTo("Test");

		// Remove competition
		test(competitionsGrid).getCellComponent(2, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = $(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id("delete-confirm-dialog-confirm").click();

		// Check if competition was removed
		assertThat(test(competitionsGrid).size()).isEqualTo(2);
	}

}
