package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.NumbersAndSheetsAthlete;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.lowagie.text.Element.ALIGN_CENTER;
import static com.lowagie.text.Element.ALIGN_MIDDLE;
import static com.lowagie.text.FontFactory.HELVETICA;
import static com.lowagie.text.PageSize.A4;

public class NumbersReport extends AbstractReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(NumbersReport.class);

	private static final float FONT_SIZE_INFO = 12f;

	private static final float FONT_SIZE_TEXT = 90f;

	private final List<NumbersAndSheetsAthlete> athletes;

	public NumbersReport(List<NumbersAndSheetsAthlete> athletes, Locale locale) {
		super(locale);
		this.athletes = athletes;
	}

	public byte[] create() {
		try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
			var document = new Document(A4, cmToPixel(1.5f), cmToPixel(1.7f), cmToPixel(0.8f), cmToPixel(0f));
			var pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();

			var i = 0;
			var number = 1;
			var table = createMainTable();
			for (var athlete : athletes) {
				if (i > 9) {
					document.add(table);
					document.newPage();
					i = 0;
					table = createMainTable();
				}
				addAthleteInfo(table, athlete, number);
				if (i % 2 == 0) {
					addEmptyCell(table);
				}
				if (i == 1 || i == 3 || i == 5 || i == 7) {
					addEmptyRow(table);
				}
				i++;
				number++;
			}
			if (number % 2 == 0) {
				// An even number indicates an odd number of athletes so we need to add an
				// empty cell to complete the row
				addEmptyCell(table);
			}

			document.add(table);

			document.close();
			pdfWriter.flush();
			return byteArrayOutputStream.toByteArray();
		}
		catch (DocumentException | IOException e) {
			LOGGER.error(e.getMessage(), e);
			return new byte[0];
		}
	}

	private PdfPTable createMainTable() {
		var table = new PdfPTable(new float[] { 10f, 1.8f, 10f });
		table.setWidthPercentage(100);
		return table;
	}

	private void addEmptyCell(PdfPTable table) {
		var cellEmpty = new PdfPCell(new Phrase(""));
		cellEmpty.setBorder(0);
		table.addCell(cellEmpty);
	}

	private void addAthleteInfo(PdfPTable table, NumbersAndSheetsAthlete athlete, int number) {
		var pdfPTable = new PdfPTable(1);
		pdfPTable.setWidthPercentage(100);

		var cellId = new PdfPCell(new Phrase(String.valueOf(number), FontFactory.getFont(HELVETICA, FONT_SIZE_TEXT)));
		cellId.setBorder(0);
		cellId.setMinimumHeight(cmToPixel(2.5f));
		cellId.setHorizontalAlignment(ALIGN_CENTER);
		pdfPTable.addCell(cellId);

		var text = "%s %s%s".formatted(athlete.lastName(), athlete.firstName(), System.lineSeparator());
		text += athlete.categoryAbbreviation();
		if (athlete.club() != null) {
			text += " / " + athlete.club();
		}

		var cellName = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA, FONT_SIZE_INFO)));
		cellName.setBorder(0);
		cellName.setMinimumHeight(cmToPixel(1.8f));
		cellName.setHorizontalAlignment(ALIGN_CENTER);
		cellName.setVerticalAlignment(ALIGN_MIDDLE);
		pdfPTable.addCell(cellName);

		var cellTable = new PdfPCell(pdfPTable);
		cellTable.setBorder(0);

		table.addCell(cellTable);
	}

	private void addEmptyRow(PdfPTable table) {
		var cellId = new PdfPCell(new Phrase(" "));
		cellId.setBorder(0);
		cellId.setMinimumHeight(cmToPixel(0.5f));
		cellId.setHorizontalAlignment(ALIGN_CENTER);
		table.addCell(cellId);

		addEmptyCell(table);

		var cellName = new PdfPCell(new Phrase(" "));
		cellName.setBorder(0);
		cellName.setMinimumHeight(cmToPixel(0.5f));
		cellName.setHorizontalAlignment(ALIGN_CENTER);
		table.addCell(cellName);
	}

}
