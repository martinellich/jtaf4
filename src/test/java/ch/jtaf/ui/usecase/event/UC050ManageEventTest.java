package ch.jtaf.ui.usecase.event;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.EventsView;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.EventDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-050: Manage event (IAAF coefficients).
 * <p>
 * See {@code docs/use_cases/uc-050-manage-event.md}.
 */
class UC050ManageEventTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigateToSeriesList();
		navigate(EventsView.class);
	}

	@Test
	void add_event() {
		// Check content of events grid
		Grid<EventRecord> eventsGrid = find(Grid.class).id("events-grid");
		assertThat(test(eventsGrid).size()).isEqualTo(17);
		assertThat(test(eventsGrid).getRow(0).getName()).isEqualTo("60 m");

		// Add event
		gridHeaderButton(eventsGrid, "edit-column").click();
		assertThat(find(EventDialog.class).all()).hasSize(1);

		test(find(TextField.class).withCaption("Abbreviation").single()).setValue("10");
		test(find(TextField.class).withCaption("Name").single()).setValue("Test");
		test(find(Select.class).withCaption("Gender").single()).selectItem("F");
		test(find(Select.class).withCaption("Event Type").single()).selectItem("RUN");
		test(find(TextField.class).withCaption("A").single()).setValue("1");
		test(find(TextField.class).withCaption("B").single()).setValue("1");
		test(find(TextField.class).withCaption("C").single()).setValue("1");
		find(Button.class).withText("Save").single().click();

		// Check if event was added
		assertThat(test(eventsGrid).size()).isEqualTo(18);
		assertThat(test(eventsGrid).getRow(0).getName()).isEqualTo("Test");

		// Remove event
		test(eventsGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = find(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		find(Button.class).id("delete-confirm-dialog-confirm").click();

		// Check if event was removed
		assertThat(test(eventsGrid).size()).isEqualTo(17);
	}

}
