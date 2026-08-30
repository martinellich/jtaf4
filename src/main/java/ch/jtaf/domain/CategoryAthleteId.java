package ch.jtaf.domain;

/**
 * Composite id of {@code CATEGORY_ATHLETE}. The component order must match the primary
 * key column order {@code (athlete_id, category_id)} because {@code JooqDAO#findById(ID)}
 * maps the record components onto the key fields positionally.
 */
public record CategoryAthleteId(long athleteId, long categoryId) {
}
