package ch.jtaf.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeriesRankingServiceTest {

	@SuppressWarnings("unused")
	@MockitoBean
	private JavaMailSender javaMailSender;

	@Autowired
	private SeriesRankingService seriesRankingService;

	@Test
	void get_club_ranking() {
		var clubRanking = seriesRankingService.getClubRanking(1L);

		assertThat(clubRanking).isPresent();
		assertThat(clubRanking.get().sortedResults()).hasSize(4);
	}

	@Test
	void create_club_ranking_pdf() {
		byte[] pdf = seriesRankingService.getClubRankingAsPdf(1L, Locale.of("de", "CH"));

		assertThat(pdf).isNotEmpty();
	}

	@Test
	void get_series_ranking() {
		var seriesRanking = seriesRankingService.getSeriesRanking(3L);

		assertThat(seriesRanking).isPresent();
		assertThat(seriesRanking.get().name()).isEqualTo("CIS 2019");
	}

	@Test
	void create_series_ranking_pdf() {
		byte[] pdf = seriesRankingService.getSeriesRankingAsPdf(3L, Locale.of("de", "CH"));

		assertThat(pdf).isNotEmpty();
	}

}
