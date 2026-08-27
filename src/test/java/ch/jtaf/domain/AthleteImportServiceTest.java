package ch.jtaf.domain;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Parsing part of UC-074: Import athletes from Excel.
 * <p>
 * See {@code docs/use_cases/uc-074-import-athletes-from-excel.md}.
 */
class AthleteImportServiceTest {

	private final AthleteImportService athleteImportService = new AthleteImportService(mock(AthleteDAO.class),
			mock(CategoryDAO.class), mock(CategoryAthleteDAO.class));

	@Test
	void parse_registrations() {
		var rows = athleteImportService.parse(workbook());

		assertThat(rows).hasSize(5);

		assertThat(rows.get(0).lastName()).isEqualTo("Woker");
		assertThat(rows.get(0).firstName()).isEqualTo("Ruben");
		assertThat(rows.get(0).yearOfBirth()).isEqualTo(2019);
		assertThat(rows.get(0).gender()).isEqualTo("M");
		assertThat(rows.get(0).status()).isEqualTo(ImportStatus.NEW);

		assertThat(rows.get(1).gender()).isEqualTo("F");
		assertThat(rows.get(1).yearOfBirth()).isEqualTo(2012);
	}

	@Test
	void parse_year_given_as_number() {
		var rows = athleteImportService.parse(workbook());

		assertThat(rows.get(2).lastName()).isEqualTo("Meier");
		assertThat(rows.get(2).yearOfBirth()).isEqualTo(2015);
		assertThat(rows.get(2).gender()).isEqualTo("M");
		assertThat(rows.get(2).status()).isEqualTo(ImportStatus.NEW);
	}

	@Test
	void parse_marks_rows_without_gender_or_year_as_invalid() {
		var rows = athleteImportService.parse(workbook());

		assertThat(rows.get(3).status()).isEqualTo(ImportStatus.INVALID);
		assertThat(rows.get(3).gender()).isNull();

		assertThat(rows.get(4).status()).isEqualTo(ImportStatus.INVALID);
		assertThat(rows.get(4).yearOfBirth()).isNull();
	}

	@Test
	void parse_stops_at_the_first_empty_row() {
		var rows = athleteImportService.parse(workbook());

		assertThat(rows).extracting(AthleteImportRow::lastName)
			.containsExactly("Woker", "Aebi", "Meier", "Ohne Geschlecht", "Ohne Jahrgang");
	}

	@Test
	void parse_returns_nothing_without_a_header_row() {
		var rows = athleteImportService.parse(workbookWithoutHeader());

		assertThat(rows).isEmpty();
	}

	private static byte[] workbook() {
		try (Workbook workbook = new XSSFWorkbook()) {
			var sheet = workbook.createSheet("Tabelle1");

			var dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd.MM.yyyy"));

			sheet.createRow(0).createCell(0).setCellValue("Jugendmeisterschaft 2025");
			sheet.createRow(1);

			var header = sheet.createRow(2);
			header.createCell(0).setCellValue("Name");
			header.createCell(1).setCellValue("Vorname");
			header.createCell(2).setCellValue("Geb. Datum");
			header.createCell(3).setCellValue("Geschlecht (männli/wiebli)");

			// Birth date as a date formatted cell, name with a trailing blank
			var first = sheet.createRow(3);
			first.createCell(0).setCellValue("Woker");
			first.createCell(1).setCellValue("Ruben ");
			var birthDate = first.createCell(2);
			birthDate.setCellValue(LocalDate.of(2019, 6, 13));
			birthDate.setCellStyle(dateStyle);
			first.createCell(3).setCellValue("m");

			var second = sheet.createRow(4);
			second.createCell(0).setCellValue("Aebi");
			second.createCell(1).setCellValue("Nia");
			var secondBirthDate = second.createCell(2);
			secondBirthDate.setCellValue(LocalDate.of(2012, 3, 1));
			secondBirthDate.setCellStyle(dateStyle);
			second.createCell(3).setCellValue("w");

			// Birth year as a plain number
			var third = sheet.createRow(5);
			third.createCell(0).setCellValue("Meier");
			third.createCell(1).setCellValue("Tim");
			third.createCell(2).setCellValue(2015);
			third.createCell(3).setCellValue("Männlich");

			var withoutGender = sheet.createRow(6);
			withoutGender.createCell(0).setCellValue("Ohne Geschlecht");
			withoutGender.createCell(1).setCellValue("Test");
			withoutGender.createCell(2).setCellValue(2015);

			var withoutYear = sheet.createRow(7);
			withoutYear.createCell(0).setCellValue("Ohne Jahrgang");
			withoutYear.createCell(1).setCellValue("Test");
			withoutYear.createCell(3).setCellValue("w");

			sheet.createRow(8);

			var afterTheEmptyRow = sheet.createRow(9);
			afterTheEmptyRow.createCell(0).setCellValue("Nach Leerzeile");
			afterTheEmptyRow.createCell(1).setCellValue("Test");
			afterTheEmptyRow.createCell(2).setCellValue(2015);
			afterTheEmptyRow.createCell(3).setCellValue("m");

			return toByteArray(workbook);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static byte[] workbookWithoutHeader() {
		try (Workbook workbook = new XSSFWorkbook()) {
			var sheet = workbook.createSheet("Tabelle1");
			var row = sheet.createRow(0);
			row.createCell(0).setCellValue("Woker");
			row.createCell(1).setCellValue("Ruben");

			return toByteArray(workbook);
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static byte[] toByteArray(Workbook workbook) throws IOException {
		var out = new ByteArrayOutputStream();
		workbook.write(out);
		return out.toByteArray();
	}

}
