package ch.jtaf.reporting.data;

import java.util.List;

public record NumbersAndSheetsAthlete(Long id, String firstName, String lastName, Integer yearOfBirth,
		String categoryAbbreviation, String categoryName, String club, List<Event> events) {

	public record Event(String name, String type) {
	}
}
