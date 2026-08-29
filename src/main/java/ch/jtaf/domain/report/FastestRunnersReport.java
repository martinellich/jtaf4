package ch.jtaf.domain.report;

import ch.jtaf.domain.data.FastestRunnersData;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.openpdf.text.PageSize.A4;

/**
 * PDF with the fastest runners of a competition (see {@link FastestRunnersData}): one
 * table for men and one for women, each limited to
 * {@link FastestRunnersData#MAX_RUNNERS}.
 */
public class FastestRunnersReport extends RankingReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(FastestRunnersReport.class);

	private static final int COLUMNS = 9;

	private final FastestRunnersData data;

	private final Document document;

	public FastestRunnersReport(FastestRunnersData data, Locale locale) {
		super(locale);
		this.data = data;

		float border = cmToPixel(1.5f);
		this.document = new Document(A4, border, border, border, border);
	}

	public byte[] create() {
		try {
			byte[] ba;
			try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
				var pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
				pdfWriter.setPageEvent(new HeaderFooter(messages.getString("Fastest.Runners"), data.name(),
						DATE_TIME_FORMATTER.format(data.competitionDate())));
				document.open();
				createRanking(messages.getString("Runners.Male"), data.ranking("M"));
				createRanking(messages.getString("Runners.Female"), data.ranking("F"));
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

	private void createRanking(String title, List<FastestRunnersData.RankedRunner> ranking) {
		var table = new PdfPTable(new float[] { 2f, 10f, 10f, 2f, 3f, 8f, 3f, 4f, 5f });
		table.setWidthPercentage(100);
		table.setSpacingBefore(cmToPixel(1f));
		table.setKeepTogether(true);

		addCategoryTitleCellWithColspan(table, title, COLUMNS);
		addCategoryTitleCellWithColspan(table, " ", COLUMNS);

		createHeaderRow(table);
		for (var ranked : ranking) {
			createRunnerRow(table, ranked);
		}
		document.add(table);
	}

	private void createHeaderRow(PdfPTable table) {
		addCell(table, messages.getString("Rank"));
		addCell(table, messages.getString("Last.Name"));
		addCell(table, messages.getString("First.Name"));
		addCell(table, messages.getString("Year"));
		addCell(table, messages.getString("Category"));
		addCell(table, messages.getString("Club"));
		addCell(table, messages.getString("Event"));
		addCellAlignRight(table, messages.getString("Time"));
		addCellAlignRight(table, messages.getString("Time.80m"));
	}

	private void createRunnerRow(PdfPTable table, FastestRunnersData.RankedRunner ranked) {
		var runner = ranked.runner();
		addCell(table, ranked.rank() + ".");
		addCell(table, runner.lastName());
		addCell(table, runner.firstName());
		addCell(table, String.valueOf(runner.yearOfBirth()));
		addCell(table, runner.category());
		addCell(table, runner.club() != null ? runner.club() : "");
		addCell(table, runner.eventAbbreviation());
		addCellAlignRight(table, runner.result());
		addCellAlignRight(table,
				runner.normalizedTime()
					.stream()
					.mapToObj(time -> String.format(Locale.ROOT, "%.2f", time))
					.findFirst()
					.orElse(""));
	}

}
