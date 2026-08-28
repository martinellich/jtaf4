package ch.jtaf.domain.report;

import ch.jtaf.domain.CategoryYears;
import ch.jtaf.domain.data.CategoriesData;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import static org.openpdf.text.FontFactory.HELVETICA_BOLD;
import static org.openpdf.text.PageSize.A4;

/**
 * One-page overview of all categories of a series: abbreviation, name, gender, birth year
 * range and the assigned events. Open ends of the birth year range (see
 * {@link CategoryYears}) are printed as an empty cell.
 */
public class CategoriesReport extends AbstractReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategoriesReport.class);

	private final CategoriesData data;

	public CategoriesReport(CategoriesData data, Locale locale) {
		super(locale);
		this.data = data;
	}

	public byte[] create() {
		try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
			var document = new Document(A4);
			var pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			pdfWriter.setPageEvent(new HeaderFooter(messages.getString("Categories"), data.seriesName(), ""));
			document.open();

			document.add(createCategoriesTable());

			document.close();
			pdfWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
		catch (DocumentException | IOException e) {
			LOGGER.error(e.getMessage(), e);
			return new byte[0];
		}
	}

	private PdfPTable createCategoriesTable() {
		var table = new PdfPTable(new float[] { 2f, 5f, 2f, 2f, 2f, 10f });
		table.setWidthPercentage(100);
		table.setSpacingBefore(cmToPixel(1f));
		table.setHeaderRows(1);

		addHeaderCell(table, messages.getString("Abbreviation"));
		addHeaderCell(table, messages.getString("Name"));
		addHeaderCell(table, messages.getString("Gender"));
		addHeaderCell(table, messages.getString("Year.From"));
		addHeaderCell(table, messages.getString("Year.To"));
		addHeaderCell(table, messages.getString("Events"));

		for (var category : data.categories()) {
			addCell(table, category.abbreviation());
			addCell(table, category.name());
			addCell(table, category.gender());
			addCell(table, formatYearFrom(category.yearFrom()));
			addCell(table, formatYearTo(category.yearTo()));
			addCell(table, String.join(", ", category.events()));
		}

		return table;
	}

	static String formatYearFrom(int yearFrom) {
		return yearFrom == CategoryYears.OPEN_FROM ? "" : String.valueOf(yearFrom);
	}

	static String formatYearTo(int yearTo) {
		return yearTo == CategoryYears.OPEN_TO ? "" : String.valueOf(yearTo);
	}

	private void addHeaderCell(PdfPTable table, String text) {
		var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA_BOLD, DEFAULT_FONT_SIZE)));
		cell.setBorder(0);
		cell.setBorderWidthBottom(1f);
		cell.setPaddingBottom(4f);
		table.addCell(cell);
	}

}
