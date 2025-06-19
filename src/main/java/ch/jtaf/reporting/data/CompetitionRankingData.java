package ch.jtaf.reporting.data;

import java.time.LocalDate;
import java.util.List;

public record CompetitionRankingData(String name, LocalDate competitionDate, boolean alwaysFirstThreeMedals,
		int medalPercentage, List<Category> categories) {

	public record Category(String abbreviation, String name, int yearFrom, int yearTo, List<Athlete> athletes) {

		public List<Athlete> sortedAthletes() {
			return athletes.stream()
				.filter(athlete -> !athlete.results.isEmpty() && !athlete.dnf)
				.sorted((o1, o2) -> Integer.compare(o2.totalPoints(), o1.totalPoints()))
				.toList();
		}

		public List<Athlete> sortedDnfAthletes() {
			return athletes.stream().filter(athlete -> athlete.dnf).toList();
		}

		public record Athlete(String firstName, String lastName, int yearOfBirth, String club, boolean dnf,
				List<Result> results) {

			public int totalPoints() {
				if (dnf) {
					return 0;
				}
				else {
					return results.stream().mapToInt(Result::points).sum();
				}
			}

			public record Result(String eventAbbreviation, String result, int points) {
			}

		}

	}

}
