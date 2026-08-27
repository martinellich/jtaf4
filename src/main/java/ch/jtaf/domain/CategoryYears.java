package ch.jtaf.domain;

/**
 * Sentinel values used in {@code CATEGORY.YEAR_FROM} and {@code CATEGORY.YEAR_TO} for the
 * open ends of a category's birth year range.
 */
public final class CategoryYears {

	/**
	 * Open lower bound — the oldest category of a series accepts every earlier birth
	 * year.
	 */
	public static final int OPEN_FROM = 1900;

	/**
	 * Open upper bound — the youngest category of a series accepts every later birth
	 * year.
	 */
	public static final int OPEN_TO = 9999;

	private CategoryYears() {
	}

}
