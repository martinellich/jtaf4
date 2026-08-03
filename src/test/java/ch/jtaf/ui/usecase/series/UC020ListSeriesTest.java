package ch.jtaf.ui.usecase.series;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import com.vaadin.flow.component.grid.Grid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.jtaf.db.tables.Series.SERIES;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-020: List series.
 * <p>
 * See {@code docs/use_cases/uc-020-list-series.md}.
 */
class UC020ListSeriesTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
	}

	@Test
	void list_series() {
		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();

		// BR-016: series of the active organization sorted by name descending
		assertThat(test(seriesGrid).size()).isEqualTo(2);
		assertThat(test(seriesGrid).getRow(0).getName()).isEqualTo("CIS 2019");
		assertThat(test(seriesGrid).getRow(1).getName()).isEqualTo("CIS 2018");

		// Name column is sortable to satisfy the "user can sort by name" step
		assertThat(seriesGrid.getColumnByKey(SERIES.NAME.getName()).isSortable()).isTrue();
	}

}
