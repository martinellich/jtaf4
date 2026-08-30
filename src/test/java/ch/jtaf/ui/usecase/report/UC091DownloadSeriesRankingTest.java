package ch.jtaf.ui.usecase.report;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.domain.SeriesRankingService;
import ch.jtaf.domain.report.SeriesRankingReport;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.DashboardView;
import ch.jtaf.usecase.UseCase;
import com.vaadin.flow.component.html.Anchor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * UC-091: Download series ranking.
 * <p>
 * The dashboard anchor {@code series-ranking-1} belongs to the first listed series
 * ("Jugendmeisterschaft 2019", id 4). The seed fixtures (hidden series 5-8, see
 * {@code V9999__Data.sql}) provide the edge cases: series 5 has no competitions, series 6
 * competitions but no results, series 7 a DNF athlete and an athlete missing a
 * competition.
 * <p>
 * See {@code docs/use_cases/uc-091-download-series-ranking.md}.
 */
class UC091DownloadSeriesRankingTest extends AbstractViewTest {

	@Autowired
	private SeriesRankingService seriesRankingService;

	@Test
	@UseCase(id = "UC-091")
	void series_ranking() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(DashboardView.class);

		Anchor anchor = find(Anchor.class).id("series-ranking-1");
		// Step 2: the download is targeted to a new browser tab
		assertThat(anchor.getTarget()).contains("_blank");

		var outputStream = new ByteArrayOutputStream();
		test(anchor).download(outputStream);

		// Step 5 / Post-S-1: a real PDF is delivered, not an empty byte array
		assertThat(outputStream.toByteArray()).isNotEmpty();
		assertThat(new String(outputStream.toByteArray(), 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
	}

	@Test
	@UseCase(id = "UC-091", businessRules = "BR-060")
	void anonymous_visitor_can_download_series_ranking() {
		setupVaadin();
		navigate(DashboardView.class);

		var outputStream = new ByteArrayOutputStream();
		test(find(Anchor.class).id("series-ranking-1")).download(outputStream);

		assertThat(outputStream.toByteArray()).isNotEmpty();
		assertThat(new String(outputStream.toByteArray(), 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
	}

	@Test
	@UseCase(id = "UC-091", businessRules = "BR-059")
	void ranking_includes_only_dnf_free_athletes_with_a_result_in_every_competition() {
		var ranking = seriesRankingService.getSeriesRanking(7L).orElseThrow();

		assertThat(ranking.numberOfCompetitions()).isEqualTo(2);
		var category = ranking.categories().getFirst();

		// The DNF athlete is excluded by the query
		assertThat(category.athletes()).extracting(a -> a.lastName())
			.containsExactlyInAnyOrder("Fixturecomplete", "Fixtureincomplete");

		// Only the athlete with a result in every competition appears in the ranking
		var ranked = category.getFilteredAndSortedAthletes(ranking.numberOfCompetitions());
		assertThat(ranked).hasSize(1);
		assertThat(ranked.getFirst().lastName()).isEqualTo("Fixturecomplete");
		assertThat(ranked.getFirst().totalPoints()).isEqualTo(980);
	}

	@Test
	@UseCase(id = "UC-091", scenario = "A1: No data")
	void series_without_competitions_has_no_ranking() {
		assertThat(seriesRankingService.getSeriesRanking(5L)).isEmpty();
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> seriesRankingService.getSeriesRankingAsPdf(5L, Locale.ENGLISH));
	}

	@Test
	@UseCase(id = "UC-091", scenario = "A1: No data")
	void series_with_competitions_but_no_results_yields_pdf_with_empty_tables() {
		byte[] pdf = seriesRankingService.getSeriesRankingAsPdf(6L, Locale.ENGLISH);

		assertThat(pdf).isNotEmpty();
		assertThat(new String(pdf, 0, 4, StandardCharsets.US_ASCII)).isEqualTo("%PDF");
	}

	@Test
	@UseCase(id = "UC-091", scenario = "Failure Postconditions")
	void unknown_series_raises_an_error() {
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> seriesRankingService.getSeriesRankingAsPdf(999L, Locale.ENGLISH));
	}

	@Test
	@UseCase(id = "UC-091", scenario = "Failure Postconditions")
	void pdf_generation_errors_are_swallowed_and_yield_an_empty_download() {
		var ranking = seriesRankingService.getSeriesRanking(7L).orElseThrow();
		var report = new SeriesRankingReport(ranking, Locale.ENGLISH);
		assertThat(report.create()).isNotEmpty();

		// A second create() on the same report fails internally because the document is
		// already closed; the DocumentException is logged and swallowed and the caller
		// receives an empty byte array — the 0-byte download of the failure
		// postcondition
		assertThat(report.create()).isEmpty();
	}

}
