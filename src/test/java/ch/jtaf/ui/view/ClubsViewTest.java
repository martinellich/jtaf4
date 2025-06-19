package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.ui.ClubsView;
import ch.jtaf.ui.KaribuTest;
import ch.jtaf.ui.dialog.ClubDialog;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class ClubsViewTest extends KaribuTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		UI.getCurrent().getPage().reload();

		navigateToSeriesList();

		UI.getCurrent().navigate(ClubsView.class);
	}

	@Test
	void add_club() {
		// Check content of clubs grid
		Grid<ClubRecord> clubsGrid = _get(Grid.class, spec -> spec.withId("clubs-grid"));
		assertThat(GridKt._size(clubsGrid)).isEqualTo(4);
		assertThat(GridKt._get(clubsGrid, 0).getName()).isEqualTo("Erlach");

		// Add new club
		_get(Button.class, spec -> spec.withId("add-button")).click();
		_assert(ClubDialog.class, 1);

		// Test maximize and restore
		Button toggle = _get(Button.class, spec -> spec.withId("toggle"));
		toggle.click();
		toggle.click();

		_get(TextField.class, spec -> spec.withLabel("Abbreviation")).setValue("Test");
		_get(TextField.class, spec -> spec.withLabel("Name")).setValue("Test");
		_get(Button.class, spec -> spec.withText("Save")).click();

		// Check if club was added
		assertThat(GridKt._size(clubsGrid)).isEqualTo(5);
		assertThat(GridKt._get(clubsGrid, 0).getName()).isEqualTo("Test");

		// Remove club
		GridKt._getCellComponent(clubsGrid, 0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
		assertThat(confirmDialog.isOpened()).isTrue();
		_get(Button.class, spec -> spec.withId("delete-confirm-dialog-confirm")).click();

		// Check if club was removed
		assertThat(GridKt._size(clubsGrid)).isEqualTo(4);
	}

}
