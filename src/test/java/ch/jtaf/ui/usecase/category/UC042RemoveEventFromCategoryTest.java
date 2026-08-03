package ch.jtaf.ui.usecase.category;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.domain.CategoryEventVO;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.dialog.CategoryDialog;
import ch.jtaf.ui.dialog.ConfirmDialog;
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
 * UC-042: Remove event from category.
 * <p>
 * See {@code docs/use_cases/uc-042-remove-event-from-category.md}.
 */
class UC042RemoveEventFromCategoryTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		assertThat(find(TextField.class).single().getValue()).isEqualTo("CIS 2019");
	}

	@Test
	void remove_event_from_category() {
		Tabs tabs = find(Tabs.class).single();
		tabs.setSelectedTab(find(Tab.class).withText("Categories").single());

		Grid<CategoryRecord> categoriesGrid = find(Grid.class).id("categories-grid");
		test(categoriesGrid).clickRow(0);
		assertThat(find(CategoryDialog.class).all()).hasSize(1);

		Grid<CategoryEventVO> categoryEventsGrid = find(Grid.class).id("category-events-grid");
		int initialSize = test(categoryEventsGrid).size();
		assertThat(initialSize).isPositive();
		String firstEventAbbreviation = test(categoryEventsGrid).getRow(0).abbreviation();

		// A1: user cancels — the assignment is kept.
		clickRemoveOnRow(categoryEventsGrid);
		ConfirmDialog confirmDialog = find(ConfirmDialog.class).id("remove-event-from-category-confirm-dialog");
		assertThat(confirmDialog.isOpened()).isTrue();
		find(Button.class).id("remove-event-from-category-confirm-dialog-cancel").click();

		assertThat(find(ConfirmDialog.class).all()).isEmpty();
		assertThat(test(categoryEventsGrid).size()).isEqualTo(initialSize);
		assertThat(test(categoryEventsGrid).getRow(0).abbreviation()).isEqualTo(firstEventAbbreviation);

		// Main flow: user confirms — the CATEGORY_EVENT row is deleted.
		clickRemoveOnRow(categoryEventsGrid);
		assertThat(find(ConfirmDialog.class).id("remove-event-from-category-confirm-dialog").isOpened()).isTrue();
		find(Button.class).id("remove-event-from-category-confirm-dialog-confirm").click();

		assertThat(test(categoryEventsGrid).size()).isEqualTo(initialSize - 1);
		for (int i = 0; i < initialSize - 1; i++) {
			assertThat(test(categoryEventsGrid).getRow(i).abbreviation()).isNotEqualTo(firstEventAbbreviation);
		}
	}

	private void clickRemoveOnRow(Grid<CategoryEventVO> categoryEventsGrid) {
		test(categoryEventsGrid).getCellComponent(0, "edit-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);
	}

}
