package ch.jtaf.reporting.report;

import org.openpdf.text.FontFactory;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;

import java.util.Locale;

import static org.openpdf.text.FontFactory.HELVETICA;
import static org.openpdf.text.FontFactory.HELVETICA_BOLD;

public class RankingReport extends AbstractReport {

	RankingReport(Locale locale) {
		super(locale);
	}

	void addCategoryTitleCellWithColspan(PdfPTable table, String text, int colspan) {
		var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA_BOLD, 12f)));
		cell.setBorder(0);
		cell.setColspan(colspan);
		table.addCell(cell);
	}

	void addResultsCell(PdfPTable table, String text) {
		var cell = new PdfPCell(new Phrase(text, FontFactory.getFont(HELVETICA, 7f)));
		cell.setColspan(5);
		cell.setBorder(0);
		cell.setPaddingBottom(8f);
		table.addCell(cell);
	}

}
