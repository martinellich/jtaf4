package ch.jtaf.domain.data;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class FastestRunnersDataTest {

	private static FastestRunnersData.Runner runner(String lastName, String gender, String event, int distance,
			String result) {
		return new FastestRunnersData.Runner("First", lastName, 2010, "A", "Club", gender, event, distance, result);
	}

	private static FastestRunnersData data(FastestRunnersData.Runner... runners) {
		return new FastestRunnersData("Test", LocalDate.of(2026, 8, 29), List.of(runners));
	}

	@Test
	void sprint_distance_is_derived_from_leading_digits_of_abbreviation() {
		assertThat(FastestRunnersData.sprintDistance("80", "80 m")).hasValue(80);
		assertThat(FastestRunnersData.sprintDistance("60", "60 m")).hasValue(60);
		assertThat(FastestRunnersData.sprintDistance("60mini", "60 m")).hasValue(60);
		assertThat(FastestRunnersData.sprintDistance("80 m", null)).hasValue(80);
	}

	@Test
	void sprint_distance_falls_back_to_name() {
		assertThat(FastestRunnersData.sprintDistance("sprint", "60 m")).hasValue(60);
		assertThat(FastestRunnersData.sprintDistance(null, "80m")).hasValue(80);
	}

	@Test
	void sprint_distance_excludes_other_events() {
		assertThat(FastestRunnersData.sprintDistance("600", "600 m")).isEmpty();
		assertThat(FastestRunnersData.sprintDistance("600+20", "600 m")).isEmpty();
		assertThat(FastestRunnersData.sprintDistance("100", "100 m")).isEmpty();
		assertThat(FastestRunnersData.sprintDistance("weit", "Weitsprung")).isEmpty();
		assertThat(FastestRunnersData.sprintDistance(null, null)).isEmpty();
	}

	@Test
	void sixty_metre_time_is_levelled_to_eighty_metres() {
		var sixty = runner("Sixty", "M", "60", 60, "9.00");
		var eighty = runner("Eighty", "M", "80", 80, "12.00");

		assertThat(sixty.normalizedTime()).hasValue(12.00);
		assertThat(eighty.normalizedTime()).hasValue(12.00);
	}

	@Test
	void ranking_mixes_both_distances_ordered_by_levelled_time() {
		var ranking = data(runner("Slow80", "M", "80", 80, "13.00"), runner("Fast60", "M", "60", 60, "9.30"),
				runner("Fast80", "M", "80", 80, "12.30"))
			.ranking("M");

		assertThat(ranking).extracting(r -> r.runner().lastName()).containsExactly("Fast80", "Fast60", "Slow80");
		assertThat(ranking).extracting(FastestRunnersData.RankedRunner::rank).containsExactly(1, 2, 3);
		assertThat(ranking.get(1).runner().normalizedTime()).hasValue(12.40);
	}

	@Test
	void equal_levelled_times_share_the_rank() {
		var ranking = data(runner("A", "F", "80", 80, "12.00"), runner("B", "F", "60", 60, "9.00"),
				runner("C", "F", "80", 80, "12.50"))
			.ranking("F");

		assertThat(ranking).extracting(FastestRunnersData.RankedRunner::rank).containsExactly(1, 1, 3);
	}

	@Test
	void ranking_is_separated_by_gender() {
		var data = data(runner("Man", "M", "80", 80, "11.00"), runner("Woman", "F", "80", 80, "12.00"));

		assertThat(data.ranking("M")).extracting(r -> r.runner().lastName()).containsExactly("Man");
		assertThat(data.ranking("F")).extracting(r -> r.runner().lastName()).containsExactly("Woman");
	}

	@Test
	void empty_and_invalid_results_are_excluded() {
		var ranking = data(runner("Empty", "M", "80", 80, ""), runner("Invalid", "M", "80", 80, "abc"),
				runner("Zero", "M", "80", 80, "0"), runner("Valid", "M", "80", 80, "12.00"))
			.ranking("M");

		assertThat(ranking).extracting(r -> r.runner().lastName()).containsExactly("Valid");
	}

	@Test
	void ranking_is_limited_to_the_fastest_fifteen_runners() {
		var runners = IntStream.rangeClosed(1, 20)
			.mapToObj(i -> runner("R" + i, "M", "80", 80, String.format(Locale.ROOT, "%.2f", 11 + i / 10.0)))
			.toArray(FastestRunnersData.Runner[]::new);

		var ranking = data(runners).ranking("M");

		assertThat(ranking).hasSize(FastestRunnersData.MAX_RUNNERS);
		assertThat(ranking.getFirst().runner().lastName()).isEqualTo("R1");
		assertThat(ranking.getLast().runner().lastName()).isEqualTo("R15");
		assertThat(ranking.getLast().rank()).isEqualTo(15);
	}

	@Test
	void runners_sharing_the_last_rank_are_all_included() {
		// 14 distinct times, then three runners tied on rank 15, then one slower runner
		var runners = IntStream.rangeClosed(1, 14)
			.mapToObj(i -> runner("R" + i, "F", "80", 80, String.format(Locale.ROOT, "%.2f", 11 + i / 10.0)))
			.collect(Collectors.toCollection(ArrayList::new));
		runners.add(runner("Tie1", "F", "80", 80, "13.00"));
		runners.add(runner("Tie2", "F", "60", 60, "9.75"));
		runners.add(runner("Tie3", "F", "80", 80, "13.00"));
		runners.add(runner("Slower", "F", "80", 80, "13.10"));

		var ranking = new FastestRunnersData("Test", LocalDate.of(2026, 8, 29), runners).ranking("F");

		assertThat(ranking).hasSize(17);
		assertThat(ranking).extracting(r -> r.runner().lastName()).doesNotContain("Slower");
		assertThat(ranking.subList(14, 17)).extracting(FastestRunnersData.RankedRunner::rank).containsOnly(15);
	}

}
