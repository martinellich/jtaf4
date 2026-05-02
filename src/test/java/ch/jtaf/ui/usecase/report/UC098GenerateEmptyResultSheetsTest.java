package ch.jtaf.ui.usecase.report;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * UC-098: Generate empty result sheets per category.
 * <p>
 * See {@code docs/use_cases/uc-098-generate-empty-result-sheets.md}.
 */
@SuppressWarnings("java:S2699")
class UC098GenerateEmptyResultSheetsTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		Tabs tabs = $(Tabs.class).single();
		Tab categories = $(Tab.class).withText("Categories").single();
		tabs.setSelectedTab(categories);
	}

	@Test
	void empty_sheets_per_category() {
		Grid<CategoryRecord> categoriesGrid = $(Grid.class).id("categories-grid");
		Anchor sheets = test(categoriesGrid).getCellComponent(0, 4)
			.getChildren()
			.filter(Anchor.class::isInstance)
			.map(Anchor.class::cast)
			.filter(anchor -> "Sheets".equals(anchor.getText()))
			.findFirst()
			.orElseThrow();
		assertThatNoException().isThrownBy(() -> test(sheets).download(new ByteArrayOutputStream()));
	}

}
