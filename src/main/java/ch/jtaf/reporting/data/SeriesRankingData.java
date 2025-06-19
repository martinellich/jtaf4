package ch.jtaf.reporting.data;

import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

import static java.lang.Integer.compare;

public record SeriesRankingData(String name, int numberOfCompetitions, List<Category> categories) {

	public record Category(String abbreviation, String name, int yearFrom, int yearTo, List<Athlete> athletes) {

		public List<Athlete> getFilteredAndSortedAthletes(int numberOfCompetitions) {
			return athletes.stream()
				.filter(athlete -> athlete.results != null && athlete.results().size() == numberOfCompetitions)
				.sorted((o1, o2) -> compare(o2.totalPoints(), o1.totalPoints()))
				.toList();
		}

		public record Athlete(String firstName, String lastName, int yearOfBirth, @Nullable String club,
				List<Result> results) {

			public int totalPoints() {
				return results.stream().map(Result::points).mapToInt(BigDecimal::intValue).sum();
			}

			public record Result(String competitionName, BigDecimal points) {
			}

		}

	}

}
