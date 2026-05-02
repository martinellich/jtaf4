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
		Grid<CategoryRecord> categoriesGrid = openCategoriesTab();
		assertCategoriesGrid(categoriesGrid, 12, "A");

		addCategory();
		assertCategoriesGrid(categoriesGrid, 13, "1");

		Grid<?> categoryEventsGrid = openAssignEventDialog(categoriesGrid);
		assertEventsGridFiltering();

		assignFirstEvent();
		assertGridSize(categoryEventsGrid, 1);

		removeFirstRow(categoryEventsGrid, "remove-event-from-category-confirm-dialog");
		assertGridSize(categoryEventsGrid, 0);

		removeFirstRow(categoriesGrid, "delete-confirm-dialog");
		assertGridSize(categoriesGrid, 12);
	}

	private Grid<CategoryRecord> openCategoriesTab() {
		Tabs tabs = $(Tabs.class).single();
		Tab categories = $(Tab.class).withText("Categories").single();
		tabs.setSelectedTab(categories);
		return $(Grid.class).id("categories-grid");
	}

	private void addCategory() {
		Grid<CategoryRecord> categoriesGrid = $(Grid.class).id("categories-grid");
		gridHeaderButton(categoriesGrid, "edit-column").click();
		assertThat($(CategoryDialog.class).all()).hasSize(1);

		test($(TextField.class).withCaption("Abbreviation").withValue("").single()).setValue("1");
		test($(TextField.class).withCaption("Name").withValue("").single()).setValue("Test");
		test($(Select.class).withCaption("Gender").single()).selectItem("M");
		test($(TextField.class).withCaption("Year from").single()).setValue("1999");
		test($(TextField.class).withCaption("Year to").single()).setValue("2000");
		$(Button.class).id("edit-save").click();
	}

	private Grid<?> openAssignEventDialog(Grid<CategoryRecord> categoriesGrid) {
		test(categoriesGrid).clickRow(0);

		Grid<?> categoryEventsGrid = $(Grid.class).id("category-events-grid");
		gridHeaderButton(categoryEventsGrid, "edit-column").click();

		Button toggle = $(Button.class).id("search-event-dialog-toggle");
		toggle.click();
		toggle.click();

		return categoryEventsGrid;
	}

	private void assertEventsGridFiltering() {
		Grid<EventRecord> eventsGrid = $(Grid.class).id("events-grid");
		assertGridSize(eventsGrid, 9);

		test($(TextField.class).id("event-filter")).setValue("w");
		assertGridSize(eventsGrid, 1);

		test($(TextField.class).id("event-filter")).setValue("2");
		assertGridSize(eventsGrid, 0);

		test($(TextField.class).id("event-filter")).setValue("");
		assertGridSize(eventsGrid, 9);
	}

	private void assignFirstEvent() {
		Grid<EventRecord> eventsGrid = $(Grid.class).id("events-grid");
		((Button) test(eventsGrid).getCellComponent(0, "assign-column")).click();
		$(SearchEventDialog.class).id("search-event-dialog").close();
	}

	private void removeFirstRow(Grid<?> grid, String confirmDialogId) {
		test(grid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = $(ConfirmDialog.class).id(confirmDialogId);
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id(confirmDialogId + "-confirm").click();
	}

	private void assertCategoriesGrid(Grid<CategoryRecord> grid, int expectedSize, String firstAbbreviation) {
		assertThat(test(grid).size()).isEqualTo(expectedSize);
		assertThat(test(grid).getRow(0).getAbbreviation()).isEqualTo(firstAbbreviation);
	}

	private void assertGridSize(Grid<?> grid, int expectedSize) {
		assertThat(test(grid).size()).isEqualTo(expectedSize);
	}

}
