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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-099: Generate categories sheet.
 * <p>
 * See {@code docs/use_cases/uc-099-generate-categories-sheet.md}.
 */
class UC099GenerateCategoriesSheetTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);

		Tabs tabs = find(Tabs.class).single();
		Tab categories = find(Tab.class).withText("Categories").single();
		tabs.setSelectedTab(categories);
	}

	@Test
	void categories_sheet() {
		Grid<CategoryRecord> categoriesGrid = find(Grid.class).id("categories-grid");
		Anchor categoriesSheet = (Anchor) categoriesGrid.getColumnByKey("sheets-column").getHeaderComponent();
		assertThat(categoriesSheet.getText()).isEqualTo("Categories sheet");

		var out = new ByteArrayOutputStream();
		test(categoriesSheet).download(out);

		assertThat(out.toByteArray()).isNotEmpty();
	}

}
