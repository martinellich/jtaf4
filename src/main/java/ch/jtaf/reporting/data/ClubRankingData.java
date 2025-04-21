package ch.jtaf.reporting.data;

import java.math.BigDecimal;
import java.util.List;

public record ClubRankingData(String seriesName, List<Result> results) {

	public List<Result> sortedResults() {
		results.sort((o1, o2) -> o2.points().compareTo(o1.points()));
		return results;
	}

	public record Result(String club, BigDecimal points) {
	}

}
