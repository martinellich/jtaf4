package ch.jtaf.ui.usecase.category;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.dialog.CategoryDialog;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.SearchEventDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-040: Manage category.
 * <p>
 * Also exercises UC-041 (Assign event to category) and UC-042 (Remove event from
 * category) — the test creates a category, assigns an event, then removes both.
 * <p>
 * See {@code docs/use_cases/uc-040-manage-category.md},
 * {@code docs/use_cases/uc-041-assign-event-to-category.md}, and
 * {@code docs/use_cases/uc-042-remove-event-from-category.md}.
 */
class UC040ManageCategoryTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		TextField name = $(TextField.class).single();
		assertThat(name.getValue()).isEqualTo("CIS 2019");
	}

	@Test
	void add_category_and_assign_event() {
		Tabs tabs = $(Tabs.class).single();
		Tab categories = $(Tab.class).withText("Categories").single();
		tabs.setSelectedTab(categories);

		// Check content of categories grid
		Grid<CategoryRecord> categoriesGrid = $(Grid.class).id("categories-grid");
		assertThat(test(categoriesGrid).size()).isEqualTo(12);
		assertThat(test(categoriesGrid).getRow(0).getAbbreviation()).isEqualTo("A");

		// Add category
		gridHeaderButton(categoriesGrid, "edit-column").click();
		assertThat($(CategoryDialog.class).all()).hasSize(1);

		test($(TextField.class).withCaption("Abbreviation").withValue("").single()).setValue("1");
		test($(TextField.class).withCaption("Name").withValue("").single()).setValue("Test");
		test($(Select.class).withCaption("Gender").single()).selectItem("M");
		test($(TextField.class).withCaption("Year from").single()).setValue("1999");
		test($(TextField.class).withCaption("Year to").single()).setValue("2000");
		$(Button.class).id("edit-save").click();

		// Check if category was added
		assertThat(test(categoriesGrid).size()).isEqualTo(13);
		assertThat(test(categoriesGrid).getRow(0).getAbbreviation()).isEqualTo("1");

		// Select category and assign event
		test(categoriesGrid).clickRow(0);

		Grid<?> categoryEventsGrid = $(Grid.class).id("category-events-grid");
		gridHeaderButton(categoryEventsGrid, "edit-column").click();

		// Test maximize and restore
		Button toggle = $(Button.class).id("search-event-dialog-toggle");
		toggle.click();
		toggle.click();

		Grid<EventRecord> eventsGrid = $(Grid.class).id("events-grid");
		assertThat(test(eventsGrid).size()).isEqualTo(9);

		// Filter with text
		test($(TextField.class).id("event-filter")).setValue("w");
		assertThat(test(eventsGrid).size()).isEqualTo(1);

		// Filter with number
		test($(TextField.class).id("event-filter")).setValue("2");
		assertThat(test(eventsGrid).size()).isZero();

		// Remove filter
		test($(TextField.class).id("event-filter")).setValue("");
		assertThat(test(eventsGrid).size()).isEqualTo(9);

		((Button) test(eventsGrid).getCellComponent(0, "assign-column")).click();

		$(SearchEventDialog.class).id("search-event-dialog").close();

		// Remove event from category
		assertThat(test(categoryEventsGrid).size()).isEqualTo(1);

		test(categoryEventsGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = $(ConfirmDialog.class).id("remove-event-from-category-confirm-dialog");
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id("remove-event-from-category-confirm-dialog-confirm").click();

		// Check if event was removed
		assertThat(test(categoryEventsGrid).size()).isZero();

		// Remove category
		test(categoriesGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		confirmDialog = $(ConfirmDialog.class).id("delete-confirm-dialog");
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id("delete-confirm-dialog-confirm").click();

		// Check if category was removed
		assertThat(test(categoriesGrid).size()).isEqualTo(12);
	}

}
