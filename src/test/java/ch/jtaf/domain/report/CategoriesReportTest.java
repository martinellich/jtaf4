package ch.jtaf.domain.report;

import ch.jtaf.domain.CategoryYears;
import ch.jtaf.domain.data.CategoriesData;
import org.junit.jupiter.api.Test;
import org.openpdf.text.pdf.PdfReader;
import org.openpdf.text.pdf.parser.PdfTextExtractor;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class CategoriesReportTest {

	@Test
	void open_year_bounds_are_printed_empty() throws IOException {
		var data = new CategoriesData("Series 2026",
				List.of(new CategoriesData.Category("A", "Knaben", "M", CategoryYears.OPEN_FROM, 2010,
						List.of("60 m", "Weit")),
						new CategoriesData.Category("B", "Knaben", "M", 2011, 2012, List.of("60 m", "Ball")),
						new CategoriesData.Category("C", "Knaben", "M", 2013, CategoryYears.OPEN_TO, List.of("50 m"))));

		byte[] pdf = new CategoriesReport(data, Locale.of("de", "CH")).create();

		assertThat(pdf).isNotEmpty();
		var text = extractText(pdf);
		assertThat(text).contains("Series 2026", "Kategorien", "Jahr von", "Jahr bis", "Disziplinen", "2010", "2011",
				"2012", "2013", "60 m, Weit", "60 m, Ball", "50 m");
		assertThat(text).doesNotContain(String.valueOf(CategoryYears.OPEN_FROM), String.valueOf(CategoryYears.OPEN_TO));
	}

	@Test
	void series_without_categories_still_produces_a_pdf() throws IOException {
		byte[] pdf = new CategoriesReport(new CategoriesData("Empty", List.of()), Locale.ENGLISH).create();

		assertThat(pdf).isNotEmpty();
		assertThat(extractText(pdf)).contains("Empty", "Categories");
	}

	private static String extractText(byte[] pdf) throws IOException {
		var reader = new PdfReader(pdf);
		try {
			var extractor = new PdfTextExtractor(reader);
			var sb = new StringBuilder();
			for (var page = 1; page <= reader.getNumberOfPages(); page++) {
				sb.append(extractor.getTextFromPage(page)).append('\n');
			}
			return sb.toString();
		}
		finally {
			reader.close();
		}
	}

}
