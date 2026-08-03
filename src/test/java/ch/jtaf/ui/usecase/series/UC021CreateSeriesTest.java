package ch.jtaf.ui.usecase.series;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.SeriesListView;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-021: Create series.
 * <p>
 * Also exercises UC-024 (Delete series) — the test creates and then removes the series.
 * <p>
 * See {@code docs/use_cases/uc-021-create-series.md} and
 * {@code docs/use_cases/uc-024-delete-series.md}.
 */
class UC021CreateSeriesTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigateToSeriesList();
	}

	@Test
	void add_series() {
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

		// Remove series
		test(seriesGrid).getCellComponent(0, "delete-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = find(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		find(Button.class).id("delete-series-confirm-dialog-confirm").click();

		// Check if series was removed
		assertThat(test(seriesGrid).size()).isEqualTo(2);
	}

}
