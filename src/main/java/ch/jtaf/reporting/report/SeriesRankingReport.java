package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.SeriesRankingData;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import static com.lowagie.text.PageSize.A4;

public class SeriesRankingReport extends RankingReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeriesRankingReport.class);

	private final SeriesRankingData ranking;

	private final Document document;

	public SeriesRankingReport(SeriesRankingData ranking, Locale locale) {
		super(locale);
		this.ranking = ranking;
		this.document = new Document(A4);
	}

	public byte[] create() {
		try {
			byte[] ba;
			try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
				var pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
				pdfWriter.setPageEvent(new HeaderFooter(messages.getString("Series.Ranking"), ranking.name(), ""));
				document.open();

				createRanking();

				document.close();
				pdfWriter.flush();
				ba = byteArrayOutputStream.toByteArray();
			}
			return ba;
		}
		catch (DocumentException | IOException e) {
			LOGGER.error(e.getMessage(), e);
			return new byte[0];
		}
	}

	private void createRanking() throws DocumentException {
		for (var category : ranking.categories()) {
			if (numberOfRows > 20) {
				document.newPage();
			}
			var table = createAthletesTable();
			createCategoryTitle(table, category);
			numberOfRows += 2;

			var rank = 1;
			for (var athlete : category.getFilteredAndSortedAthletes(ranking.numberOfCompetitions())) {
				if (numberOfRows > 23) {
					document.add(table);
					document.newPage();
					table = createAthletesTable();
				}
				createAthleteRow(table, rank, athlete);
				rank++;
				numberOfRows += 1;
			}
			document.add(table);
		}
	}

	private PdfPTable createAthletesTable() {
		var table = new PdfPTable(new float[] { 3f, 10f, 10f, 2f, 5f, 5f });
		table.setWidthPercentage(100);
		table.setSpacingBefore(cmToPixel(1f));
		return table;
	}

	private void createCategoryTitle(PdfPTable table, SeriesRankingData.Category category) {
		addCategoryTitleCellWithColspan(table, category.abbreviation(), 1);
		addCategoryTitleCellWithColspan(table, category.name() + " " + category.yearFrom() + " - " + category.yearTo(),
				5);

		addCategoryTitleCellWithColspan(table, " ", 6);
	}

	private void createAthleteRow(PdfPTable table, int rank, SeriesRankingData.Category.Athlete athlete) {
		addCell(table, rank + ".");
		addCell(table, athlete.lastName());
		addCell(table, athlete.firstName());
		addCell(table, String.valueOf(athlete.yearOfBirth()));
		addCell(table, athlete.club() != null ? athlete.club() : "");
		addCellAlignRight(table, String.valueOf(athlete.totalPoints()));

		var sb = new StringBuilder();
		for (var result : athlete.results()) {
			sb.append(result.competitionName());
			sb.append(": ");
			sb.append(result.points());
			sb.append(" ");
		}
		addCell(table, "");
		addResultsCell(table, sb.toString());
	}

}
