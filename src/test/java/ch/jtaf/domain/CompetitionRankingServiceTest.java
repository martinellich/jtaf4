package ch.jtaf.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CompetitionRankingServiceTest {

	@MockitoBean
	private JavaMailSender javaMailSender;

	@Autowired
	private CompetitionRankingService competitionRankingService;

	@Test
	void get_competition_ranking() {
		var competitionRanking = competitionRankingService.getCompetitionRanking(6L);

		assertThat(competitionRanking).isPresent();
		assertThat(competitionRanking.get().name()).isEqualTo("39. Jugendmeisterschaft");
	}

	@Test
	void create_competition_ranking_pdf() {
		byte[] pdf = competitionRankingService.getCompetitionRankingAsPdf(6L, Locale.of("de", "CH"));

		assertThat(pdf).isNotEmpty();
	}

	@Test
	void get_events_ranking() {
		var eventsRanking = competitionRankingService.getEventsRanking(6L);

		assertThat(eventsRanking).isPresent();
		assertThat(eventsRanking.get().name()).isEqualTo("39. Jugendmeisterschaft");
	}

	@Test
	void get_events_ranking_pdf() {
		byte[] pdf = competitionRankingService.getEventRankingAsPdf(6L, Locale.of("de", "CH"));

		assertThat(pdf).isNotEmpty();
	}

	@Test
	void get_diplomas_pdf() {
		byte[] pdf = competitionRankingService.getDiplomasAsPdf(6L, Locale.of("de", "CH"));

		assertThat(pdf).isNotEmpty();
	}

}
