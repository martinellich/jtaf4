package ch.jtaf.domain;

import java.util.List;

/**
 * Summary of an athlete import.
 *
 * @param created number of athletes newly created in the organization
 * @param existing number of athletes that were found and reused
 * @param assigned number of athletes assigned to a category of the series
 * @param withoutCategory athletes for which no category of the series matched
 * @param invalid rows that could not be read
 */
public record AthleteImportResult(int created, int existing, int assigned, List<String> withoutCategory,
		List<String> invalid) {
}
