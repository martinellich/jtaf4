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
 * UC-024: Delete series.
 * <p>
 * See {@code docs/use_cases/uc-024-delete-series.md}.
 */
class UC024DeleteSeriesTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigateToSeriesList();
	}

	@Test
	void delete_series() {
		// Add a deletable series — the seeded series have dependent competitions/results.
		Grid<SeriesRecord> initialGrid = $(Grid.class).id("series-grid");
		gridHeaderButton(initialGrid, "delete-column").click();

		test($(TextField.class).single()).setValue("To be deleted");
		$(Button.class).id("save-series").click();
		$(Notification.class).single().close();

		navigate(SeriesListView.class);

		Grid<SeriesRecord> seriesGrid = $(Grid.class).id("series-grid");
		assertThat(test(seriesGrid).size()).isEqualTo(3);
		assertThat(test(seriesGrid).getRow(0).getName()).isEqualTo("To be deleted");

		// A2: user cancels — dialog closes and the series remains.
		clickDeleteOnRow(seriesGrid);
		ConfirmDialog confirmDialog = $(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id("delete-series-confirm-dialog-cancel").click();

		assertThat($(ConfirmDialog.class).all()).isEmpty();
		assertThat(test(seriesGrid).size()).isEqualTo(3);

		// Main flow: user confirms — the series and its categories are deleted.
		clickDeleteOnRow(seriesGrid);
		assertThat($(ConfirmDialog.class).single().isOpened()).isTrue();
		$(Button.class).id("delete-series-confirm-dialog-confirm").click();

		assertThat(test(seriesGrid).size()).isEqualTo(2);
		assertThat(test(seriesGrid).getRow(0).getName()).isEqualTo("CIS 2019");
	}

	private void clickDeleteOnRow(Grid<SeriesRecord> seriesGrid) {
		test(seriesGrid).getCellComponent(0, "delete-column")
			.getChildren()
			.filter(Button.class::isInstance)
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);
	}

}
