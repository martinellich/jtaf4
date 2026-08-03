package ch.jtaf.ui.usecase.category;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.domain.CategoryEventVO;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.dialog.CategoryDialog;
import ch.jtaf.ui.dialog.SearchEventDialog;
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
 * UC-041: Assign event to category.
 * <p>
 * See {@code docs/use_cases/uc-041-assign-event-to-category.md}.
 */
class UC041AssignEventToCategoryTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		assertThat(find(TextField.class).single().getValue()).isEqualTo("CIS 2019");
	}

	@Test
	void assign_event_to_category() {
		Tabs tabs = find(Tabs.class).single();
		tabs.setSelectedTab(find(Tab.class).withText("Categories").single());

		Grid<CategoryRecord> categoriesGrid = find(Grid.class).id("categories-grid");
		test(categoriesGrid).clickRow(0);
		assertThat(find(CategoryDialog.class).all()).hasSize(1);

		Grid<CategoryEventVO> categoryEventsGrid = find(Grid.class).id("category-events-grid");
		int initialSize = test(categoryEventsGrid).size();
		int maxPosition = test(categoryEventsGrid).getRow(initialSize - 1).position();

		// Open the search dialog and assign an unassigned event.
		gridHeaderButton(categoryEventsGrid, "edit-column").click();
		assertThat(find(SearchEventDialog.class).all()).hasSize(1);

		test(find(TextField.class).id("event-filter")).setValue("ball");
		Grid<EventRecord> eventsGrid = find(Grid.class).id("events-grid");
		assertThat(test(eventsGrid).size()).isEqualTo(1);
		assertThat(test(eventsGrid).getRow(0).getAbbreviation()).isEqualTo("ball");

		((Button) test(eventsGrid).getCellComponent(0, "assign-column")).click();
		find(SearchEventDialog.class).id("search-event-dialog").close();

		// Postcondition: event count grew by one and BR-034 sets the next position.
		assertThat(test(categoryEventsGrid).size()).isEqualTo(initialSize + 1);
		CategoryEventVO assigned = test(categoryEventsGrid).getRow(initialSize);
		assertThat(assigned.abbreviation()).isEqualTo("ball");
		assertThat(assigned.position()).isEqualTo(maxPosition + 1);

		// Reopening the search dialog must no longer offer the just-assigned event.
		gridHeaderButton(categoryEventsGrid, "edit-column").click();
		test(find(TextField.class).id("event-filter")).setValue("ball");
		assertThat(test(find(Grid.class).id("events-grid")).size()).isZero();
		find(SearchEventDialog.class).id("search-event-dialog").close();
	}

}
