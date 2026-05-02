package ch.jtaf.ui.usecase.athlete;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.AthletesView;
import ch.jtaf.ui.dialog.AthleteDialog;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-070: Manage athlete.
 * <p>
 * See {@code docs/use_cases/uc-070-manage-athlete.md}.
 */
class UC070ManageAthleteTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigateToSeriesList();
		navigate(AthletesView.class);
	}

	@Test
	void add_athlete() {
		// Check content of athletes grid
		Grid<AthleteRecord> athletesGrid = $(Grid.class).id("athletes-grid");
		assertThat(test(athletesGrid).size()).isEqualTo(140);
		assertThat(test(athletesGrid).getRow(0).getLastName()).isEqualTo("Bangerter");

		// Add a new athlete
		gridHeaderButton(athletesGrid, "edit-column").click();
		assertThat($(AthleteDialog.class).all()).hasSize(1);

		test($(TextField.class).withCaption("Last Name").single()).setValue("Test");
		test($(TextField.class).withCaption("First Name").single()).setValue("Test");
		test($(Select.class).withCaption("Gender").single()).selectItem("F");
		test($(TextField.class).withCaption("Year").single()).setValue("2000");
		$(Button.class).withText("Save").single().click();

		// Check if athlete was added
		assertThat(test(athletesGrid).size()).isEqualTo(141);
		assertThat(test(athletesGrid).getRow(0).getLastName()).isEqualTo("Test");

		// Remove athlete
		test(athletesGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = $(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id("delete-confirm-dialog-confirm").click();

		// Check that athlete was removed
		assertThat(test(athletesGrid).size()).isEqualTo(140);
	}

}
