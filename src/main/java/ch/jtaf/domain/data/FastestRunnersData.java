package ch.jtaf.domain.data;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.regex.Pattern;

/**
 * Fastest runners of a competition across the 80 m and 60 m sprint events. 60 m times are
 * levelled to 80 m by multiplying with 80/60 so that both events can be ranked together.
 */
public record FastestRunnersData(String name, LocalDate competitionDate, List<Runner> runners) {

	public static final int REFERENCE_DISTANCE = 80;

	private static final Pattern SPRINT_DISTANCE = Pattern.compile("^(60|80)(\\D.*)?$");

	/**
	 * Determines the sprint distance of an event from its abbreviation (fallback: name).
	 * Only 60 m and 80 m count; the leading digits must be exactly 60 or 80 so that e.g.
	 * "600" or "600+20" are excluded while "60mini" or "80 m" are accepted.
	 */
	public static OptionalInt sprintDistance(@Nullable String abbreviation, @Nullable String name) {
		for (var candidate : new String[] { abbreviation, name }) {
			if (candidate != null) {
				var matcher = SPRINT_DISTANCE.matcher(candidate.strip());
				if (matcher.matches()) {
					return OptionalInt.of(Integer.parseInt(matcher.group(1)));
				}
			}
		}
		return OptionalInt.empty();
	}

	/**
	 * Ranking of the runners of the given gender ordered by levelled time. Runners with
	 * the same levelled time share the rank (1, 1, 3).
	 */
	public List<RankedRunner> ranking(String gender) {
		var sorted = runners.stream()
			.filter(runner -> gender.equals(runner.gender()))
			.filter(runner -> runner.normalizedTime().isPresent())
			.sorted(Comparator.comparingDouble(runner -> runner.normalizedTime().orElseThrow()))
			.toList();

		var ranking = new ArrayList<RankedRunner>(sorted.size());
		var rank = 0;
		var previousTime = Double.NaN;
		for (var i = 0; i < sorted.size(); i++) {
			var runner = sorted.get(i);
			var time = runner.normalizedTime().orElseThrow();
			if (Double.compare(time, previousTime) != 0) {
				rank = i + 1;
				previousTime = time;
			}
			ranking.add(new RankedRunner(rank, runner));
		}
		return ranking;
	}

	public record RankedRunner(int rank, Runner runner) {
	}

	public record Runner(String firstName, String lastName, int yearOfBirth, String category, @Nullable String club,
			String gender, String eventAbbreviation, int distance, String result) {

		/**
		 * The measured time in seconds or empty if the result is missing or not numeric.
		 */
		public OptionalDouble time() {
			try {
				var time = Double.parseDouble(result.strip());
				return time > 0 ? OptionalDouble.of(time) : OptionalDouble.empty();
			}
			catch (NumberFormatException _) {
				return OptionalDouble.empty();
			}
		}

		/**
		 * The time levelled to {@link #REFERENCE_DISTANCE} metres:
		 * {@code time * 80 / distance}.
		 */
		public OptionalDouble normalizedTime() {
			var time = time();
			if (time.isEmpty() || distance <= 0) {
				return OptionalDouble.empty();
			}
			var normalized = time.getAsDouble() * REFERENCE_DISTANCE / distance;
			// round to centiseconds so equal levelled times are detected as ties
			return OptionalDouble.of(Math.round(normalized * 100) / 100.0);
		}

	}

}
