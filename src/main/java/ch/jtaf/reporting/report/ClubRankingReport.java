package ch.jtaf.reporting.report;

import ch.jtaf.reporting.data.ClubRankingData;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import static org.openpdf.text.PageSize.A4;

public class ClubRankingReport extends RankingReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClubRankingReport.class);

	private final ClubRankingData ranking;

	private final Document document;

	public ClubRankingReport(ClubRankingData ranking, Locale locale) {
		super(locale);
		this.ranking = ranking;

		var border = cmToPixel(1.5f);
		this.document = new Document(A4, border, border, border, border);
	}

	public byte[] create() {
		try {
			try (var byteArrayOutputStream = new ByteArrayOutputStream()) {
				var pdfWriter = PdfWriter.getInstance(document, byteArrayOutputStream);
				pdfWriter.setPageEvent(new HeaderFooter(messages.getString("Club.Ranking"), ranking.seriesName(), ""));

				document.open();

				createRanking();

				document.close();

				pdfWriter.flush();
				return byteArrayOutputStream.toByteArray();
			}
		}
		catch (IOException | DocumentException e) {
			LOGGER.error(e.getMessage(), e);
			return new byte[0];
		}
	}

	private void createRanking() {
		var table = new PdfPTable(new float[] { 2f, 10f, 10f });
		table.setWidthPercentage(100f);
		table.setSpacingBefore(1f);

		var rank = 1;
		for (var result : ranking.sortedResults()) {
			createClubRow(table, result, rank);
			rank++;
		}

		document.add(table);
	}

	private void createClubRow(PdfPTable table, ClubRankingData.Result result, int rank) {
		addCell(table, rank + ".");
		addCell(table, result.club());
		addCellAlignRight(table, result.points().toString());
	}

}
