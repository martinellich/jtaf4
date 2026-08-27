package ch.jtaf.ui.usecase.athlete;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.domain.AthleteImportRow;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.SeriesView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.upload.Upload;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.IntStream;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.impl.DSL.select;

/**
 * UC-074: Import athletes from Excel.
 * <p>
 * See {@code docs/use_cases/uc-074-import-athletes-from-excel.md}.
 */
class UC074ImportAthletesFromExcelTest extends AbstractViewTest {

	private static final long ORGANIZATION_ID = 1L;

	private static final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	@Autowired
	private DSLContext dslContext;

	private Long seriesId;

	@BeforeEach
	void createEmptySeries() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		// Selects the active organization; the seeded series count is asserted there, so
		// the test series is created afterwards.
		navigateToSeriesList();

		var series = SERIES.newRecord();
		series.setName("Import Test");
		series.setHidden(true);
		series.setLocked(false);
		series.setOrganizationId(ORGANIZATION_ID);
		series.attach(dslContext.configuration());
		series.store();

		seriesId = series.getId();
	}

	@AfterEach
	void removeTestData() {
		dslContext.deleteFrom(CATEGORY_ATHLETE)
			.where(CATEGORY_ATHLETE.CATEGORY_ID
				.in(select(CATEGORY.ID).from(CATEGORY).where(CATEGORY.SERIES_ID.eq(seriesId))))
			.execute();
		dslContext.deleteFrom(CATEGORY).where(CATEGORY.SERIES_ID.eq(seriesId)).execute();
		dslContext.deleteFrom(SERIES).where(SERIES.ID.eq(seriesId)).execute();
		dslContext.deleteFrom(ATHLETE)
			.where(ATHLETE.ORGANIZATION_ID.eq(ORGANIZATION_ID))
			.and(ATHLETE.LAST_NAME.eq("Muster"))
			.execute();
	}

	@Test
	void import_athletes_into_series() {
		createCategories();

		Grid<AthleteRecord> athletesGrid = openAthletesTab();

		gridHeaderButton(athletesGrid, "remove-column", "import-athletes").click();

		uploadRegistrations();

		Grid<AthleteImportRow> previewGrid = find(Grid.class).id("import-preview-grid");
		assertThat(test(previewGrid).size()).isEqualTo(3);

		// The preview starts out in the order of the file and can be sorted by every
		// column
		assertThat(rowsOf(previewGrid)).containsExactly("Zimmermann", "Muster", "Fehler");

		previewGrid.sort(GridSortOrder.asc(previewGrid.getColumnByKey("last-name")).build());
		assertThat(rowsOf(previewGrid)).containsExactly("Fehler", "Muster", "Zimmermann");

		previewGrid.sort(GridSortOrder.desc(previewGrid.getColumnByKey("year-of-birth")).build());
		assertThat(rowsOf(previewGrid)).startsWith("Muster", "Fehler");

		Span summary = find(Span.class).id("import-summary");
		assertThat(summary.getText()).isEqualTo("1 new, 1 existing, 0 without category");

		find(Button.class).id("import-athletes-import").click();

		Notification notification = find(Notification.class).single();
		assertThat(test(notification).getText()).isEqualTo("2 athletes assigned, 1 newly created, 0 without category");
		notification.close();

		assertThat(test(athletesGrid).size()).isEqualTo(2);
		assertThat(test(athletesGrid).getRow(0).getLastName()).isEqualTo("Muster");
		assertThat(test(athletesGrid).getRow(1).getLastName()).isEqualTo("Zimmermann");

		// A second import of the same file must not create or assign anything twice
		gridHeaderButton(athletesGrid, "remove-column", "import-athletes").click();
		uploadRegistrations();

		assertThat(find(Span.class).id("import-summary").getText()).isEqualTo("0 new, 2 existing, 0 without category");

		find(Button.class).id("import-athletes-import").click();

		Notification secondNotification = find(Notification.class).single();
		assertThat(test(secondNotification).getText())
			.isEqualTo("0 athletes assigned, 0 newly created, 0 without category");
		secondNotification.close();

		assertThat(test(find(Grid.class).id("athletes-grid")).size()).isEqualTo(2);
	}

	@Test
	void import_reports_athletes_without_a_matching_category() {
		Grid<AthleteRecord> athletesGrid = openAthletesTab();

		gridHeaderButton(athletesGrid, "remove-column", "import-athletes").click();

		uploadRegistrations();

		Span summary = find(Span.class).id("import-summary");
		assertThat(summary.getText()).isEqualTo("0 new, 0 existing, 2 without category");

		find(Button.class).id("import-athletes-import").click();

		Notification notification = find(Notification.class).single();
		assertThat(test(notification).getText()).isEqualTo("0 athletes assigned, 1 newly created, 2 without category");
		notification.close();

		assertThat(test(find(Grid.class).id("athletes-grid")).size()).isZero();
	}

	private void createCategories() {
		createCategory("K", "Knaben", "M");
		createCategory("M", "Mädchen", "F");
	}

	private void createCategory(String abbreviation, String name, String gender) {
		var category = CATEGORY.newRecord();
		category.setAbbreviation(abbreviation);
		category.setName(name);
		category.setGender(gender);
		category.setYearFrom(2011);
		category.setYearTo(9999);
		category.setSeriesId(seriesId);
		category.attach(dslContext.configuration());
		category.store();
	}

	private Grid<AthleteRecord> openAthletesTab() {
		navigate(SeriesView.class, seriesId);

		Tabs tabs = find(Tabs.class).single();
		tabs.setSelectedTab(find(Tab.class).withText("Athletes").single());

		return find(Grid.class).id("athletes-grid");
	}

	private void uploadRegistrations() {
		Upload upload = find(Upload.class).id("import-athletes-upload");
		test(upload).upload("registrations.xlsx", XLSX_MIME_TYPE, registrations());
	}

	private static byte[] registrations() {
		try (Workbook workbook = new XSSFWorkbook()) {
			var sheet = workbook.createSheet("Tabelle1");

			sheet.createRow(0).createCell(0).setCellValue("Import Test");
			sheet.createRow(1);

			var header = sheet.createRow(2);
			header.createCell(0).setCellValue("Name");
			header.createCell(1).setCellValue("Vorname");
			header.createCell(2).setCellValue("Geb. Datum");
			header.createCell(3).setCellValue("Geschlecht (männli/wiebli)");

			// Already registered in the organization
			var existing = sheet.createRow(3);
			existing.createCell(0).setCellValue("Zimmermann");
			existing.createCell(1).setCellValue("Kendall");
			existing.createCell(2).setCellValue(2011);
			existing.createCell(3).setCellValue("w");

			// Unknown athlete
			var created = sheet.createRow(4);
			created.createCell(0).setCellValue("Muster");
			created.createCell(1).setCellValue("Hans");
			created.createCell(2).setCellValue(2015);
			created.createCell(3).setCellValue("m");

			// Unreadable gender
			var invalid = sheet.createRow(5);
			invalid.createCell(0).setCellValue("Fehler");
			invalid.createCell(1).setCellValue("Test");
			invalid.createCell(2).setCellValue(2015);
			invalid.createCell(3).setCellValue("?");

			var out = new ByteArrayOutputStream();
			workbook.write(out);
			return out.toByteArray();
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private List<String> rowsOf(Grid<AthleteImportRow> previewGrid) {
		return IntStream.range(0, test(previewGrid).size())
			.mapToObj(row -> test(previewGrid).getRow(row).lastName())
			.toList();
	}

}
