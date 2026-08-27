package ch.jtaf.domain;

/**
 * Result of analysing a single row of an athlete import file.
 */
public enum ImportStatus {

	/**
	 * The athlete does not exist yet and will be created and assigned.
	 */
	NEW,

	/**
	 * The athlete already exists in the organization and will be assigned.
	 */
	EXISTING,

	/**
	 * The athlete already exists and is already assigned to a category of the series.
	 */
	ALREADY_ASSIGNED,

	/**
	 * No category of the series matches the gender and year of birth; the athlete is
	 * created or reused but not assigned.
	 */
	NO_CATEGORY,

	/**
	 * The row could not be read (missing name, year of birth or gender).
	 */
	INVALID

}
