package ch.jtaf.domain.data;

import java.util.List;

public record CategoriesData(String seriesName, List<Category> categories) {

	public record Category(String abbreviation, String name, String gender, int yearFrom, int yearTo,
			List<String> events) {
	}
}
