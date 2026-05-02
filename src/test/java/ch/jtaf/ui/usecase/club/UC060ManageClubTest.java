package ch.jtaf.ui.usecase.club;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.ClubsView;
import ch.jtaf.ui.dialog.ClubDialog;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-060: Manage club.
 * <p>
 * See {@code docs/use_cases/uc-060-manage-club.md}.
 */
class UC060ManageClubTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigateToSeriesList();
		navigate(ClubsView.class);
	}

	@Test
	void add_club() {
		// Check content of clubs grid
		Grid<ClubRecord> clubsGrid = $(Grid.class).id("clubs-grid");
		assertThat(test(clubsGrid).size()).isEqualTo(4);
		assertThat(test(clubsGrid).getRow(0).getName()).isEqualTo("Erlach");

		// Add new club
		gridHeaderButton(clubsGrid, "edit-column").click();
		assertThat($(ClubDialog.class).all()).hasSize(1);

		// Test maximize and restore
		Button toggle = $(Button.class).id("toggle");
		toggle.click();
		toggle.click();

		test($(TextField.class).withCaption("Abbreviation").single()).setValue("Test");
		test($(TextField.class).withCaption("Name").single()).setValue("Test");
		$(Button.class).withText("Save").single().click();

		// Check if club was added
		assertThat(test(clubsGrid).size()).isEqualTo(5);
		assertThat(test(clubsGrid).getRow(0).getName()).isEqualTo("Test");

		// Remove club
		test(clubsGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = $(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id("delete-confirm-dialog-confirm").click();

		// Check if club was removed
		assertThat(test(clubsGrid).size()).isEqualTo(4);
	}

}
