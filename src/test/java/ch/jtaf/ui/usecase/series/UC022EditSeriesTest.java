package ch.jtaf.ui.usecase.series;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-022: Edit series.
 * <p>
 * See {@code docs/use_cases/uc-022-edit-series.md}.
 */
class UC022EditSeriesTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);
	}

	@Test
	void edit_series() {
		TextField name = $(TextField.class).single();
		assertThat(name.getValue()).isEqualTo("CIS 2019");

		Checkbox hidden = $(Checkbox.class).withCaption("Hidden").single();
		Checkbox locked = $(Checkbox.class).withCaption("Locked").single();
		assertThat(hidden.getValue()).isFalse();
		assertThat(locked.getValue()).isFalse();

		test(name).setValue("CIS 2019 Renamed");
		test(hidden).click();
		test(locked).click();
		assertThat(hidden.getValue()).isTrue();
		assertThat(locked.getValue()).isTrue();

		$(Button.class).id("save-series").click();

		Notification savedNotification = $(Notification.class).single();
		assertThat(test(savedNotification).getText()).isEqualTo("Series saved");
		savedNotification.close();

		// Restore original values
		test($(TextField.class).single()).setValue("CIS 2019");
		test($(Checkbox.class).withCaption("Hidden").single()).click();
		test($(Checkbox.class).withCaption("Locked").single()).click();
		$(Button.class).id("save-series").click();
		$(Notification.class).single().close();
	}

}
