package ch.jtaf.domain;

import org.jspecify.annotations.Nullable;

/**
 * One row of an athlete import file together with the status determined by
 * {@link AthleteImportService#analyze(byte[], long, long)}.
 *
 * @param rowNumber the one based row number in the sheet, used to report invalid rows
 * @param lastName the family name, trimmed
 * @param firstName the given name, trimmed
 * @param yearOfBirth the year of birth or {@code null} if it could not be read
 * @param gender {@code M} or {@code F} or {@code null} if it could not be read
 * @param status what will happen to this row on import
 */
public record AthleteImportRow(int rowNumber, String lastName, String firstName, @Nullable Integer yearOfBirth,
		@Nullable String gender, ImportStatus status) {

	public AthleteImportRow withStatus(ImportStatus newStatus) {
		return new AthleteImportRow(rowNumber, lastName, firstName, yearOfBirth, gender, newStatus);
	}

}
