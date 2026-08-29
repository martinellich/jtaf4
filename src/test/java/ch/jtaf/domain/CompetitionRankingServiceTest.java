package ch.jtaf.domain;

import ch.jtaf.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class CompetitionRankingServiceTest {

	@SuppressWarnings("unused")
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
	void get_fastest_runners() {
		// competition 1 has 80 m and 60 m results for both genders
		var fastestRunners = competitionRankingService.getFastestRunners(1L);

		assertThat(fastestRunners).isPresent();
		assertThat(fastestRunners.get().name()).isEqualTo("1. CIS Twann");
		assertThat(fastestRunners.get().runners()).isNotEmpty()
			.allSatisfy(runner -> assertThat(runner.distance()).isIn(60, 80));

		var men = fastestRunners.get().ranking("M");
		var women = fastestRunners.get().ranking("F");
		assertThat(men).isNotEmpty().allSatisfy(ranked -> assertThat(ranked.runner().gender()).isEqualTo("M"));
		assertThat(women).isNotEmpty().allSatisfy(ranked -> assertThat(ranked.runner().gender()).isEqualTo("F"));
		assertThat(men.getFirst().rank()).isEqualTo(1);
		assertThat(men).isSortedAccordingTo((a, b) -> Double.compare(a.runner().normalizedTime().orElseThrow(),
				b.runner().normalizedTime().orElseThrow()));
	}

	@Test
	void get_fastest_runners_pdf() {
		byte[] pdf = competitionRankingService.getFastestRunnersAsPdf(1L, Locale.of("de", "CH"));

		assertThat(pdf).isNotEmpty();
	}

	@Test
	void get_diplomas_pdf() {
		// competition 6 has medal_percentage = 50, so there are medal winners
		byte[] pdf = competitionRankingService.getDiplomasAsPdf(6L, Locale.of("de", "CH"));

		assertThat(pdf).isNotEmpty();
	}

	@Test
	void get_diplomas_pdf_without_medals() {
		// competition 1 has medal_percentage = 0, so no diplomas are generated
		byte[] pdf = competitionRankingService.getDiplomasAsPdf(1L, Locale.of("de", "CH"));

		assertThat(pdf).isEmpty();
	}

}
