package ch.jtaf.ui.usecase.series;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.SeriesListView;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-025: Copy categories from another series.
 * <p>
 * See {@code docs/use_cases/uc-025-copy-categories.md}.
 */
class UC025CopyCategoriesTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigateToSeriesList();
	}

	@Test
	void add_series_and_copy_categories() {
		// Add new series
		Grid<SeriesRecord> initialGrid = find(Grid.class).id("series-grid");
		gridHeaderButton(initialGrid, "delete-column").click();

		TextField textField = find(TextField.class).single();
		assertThat(textField.getValue()).isEmpty();

		test(textField).setValue("Test");

		find(Button.class).id("save-series").click();

		Notification savedNotification = find(Notification.class).single();
		assertThat(test(savedNotification).getText()).isEqualTo("Series saved");
		savedNotification.close();

		navigate(SeriesListView.class);

		// Check if series was added
		Grid<SeriesRecord> seriesGrid = find(Grid.class).id("series-grid");
		assertThat(test(seriesGrid).size()).isEqualTo(3);
		assertThat(test(seriesGrid).getRow(0).getName()).isEqualTo("Test");

		// Navigate to SeriesView
		test(seriesGrid).clickRow(0);

		// Copy series
		find(Button.class).id("copy-categories").click();

		ComboBox<SeriesRecord> seriesSelection = find(ComboBox.class).id("series-selection");
		seriesSelection.setValue(seriesSelection.getLazyDataView().getItem(0));
		find(Button.class).id("copy-categories-copy").click();

		Notification copiedNotification = find(Notification.class).single();
		assertThat(test(copiedNotification).getText()).isEqualTo("Categories copied");
		copiedNotification.close();

		// Select Categories tab
		Tabs tabs = find(Tabs.class).single();
		Tab categories = find(Tab.class).withText("Categories").single();
		tabs.setSelectedTab(categories);

		// Check if categories have been copied
		Grid<CategoryRecord> categoriesGrid = find(Grid.class).id("categories-grid");
		assertThat(test(categoriesGrid).size()).isEqualTo(12);
		assertThat(test(categoriesGrid).getRow(0).getAbbreviation()).isEqualTo("A");

		// Remove series
		navigate(SeriesListView.class);
		Grid<SeriesRecord> refreshedGrid = find(Grid.class).id("series-grid");

		test(refreshedGrid).getCellComponent(0, "delete-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = find(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		find(Button.class).id("delete-series-confirm-dialog-confirm").click();

		// Check if series was removed
		assertThat(test(refreshedGrid).size()).isEqualTo(2);
	}

}
