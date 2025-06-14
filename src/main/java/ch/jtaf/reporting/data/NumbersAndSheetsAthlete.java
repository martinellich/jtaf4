package ch.jtaf.reporting.data;

import org.jspecify.annotations.Nullable;

import java.util.List;

public record NumbersAndSheetsAthlete(@Nullable Long id, @Nullable String firstName, @Nullable String lastName,
		@Nullable Integer yearOfBirth, String categoryAbbreviation, String categoryName, @Nullable String club,
		List<Event> events) {

	public record Event(String name, String type) {
	}
}
