package ch.jtaf.ui.usecase.report;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * UC-097: Generate result sheets.
 * <p>
 * See {@code docs/use_cases/uc-097-generate-result-sheets.md}.
 */
@SuppressWarnings("java:S2699")
class UC097GenerateResultSheetsTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);
	}

	@Test
	void sheets_ordered_by_athlete() {
		Grid<CompetitionRecord> competitionsGrid = find(Grid.class).id("competitions-grid");
		Anchor sheets = test(competitionsGrid).getCellComponent(0, 2)
			.getChildren()
			.filter(Anchor.class::isInstance)
			.map(Anchor.class::cast)
			.filter(anchor -> "Sheets".equals(anchor.getText()))
			.findFirst()
			.orElseThrow();
		assertThatNoException().isThrownBy(() -> test(sheets).download(new ByteArrayOutputStream()));
	}

}
