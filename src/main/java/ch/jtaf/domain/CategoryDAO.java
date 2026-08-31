package ch.jtaf.domain;

import ch.jtaf.db.tables.Category;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.martinelli.oss.jooqspring.JooqDAO;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static ch.jtaf.db.tables.Category.CATEGORY;

// @formatter:off
@Repository
public class CategoryDAO extends JooqDAO<Category, CategoryRecord, Long> {

    public CategoryDAO(DSLContext dslContext) {
        super(dslContext, CATEGORY);
    }

    public List<CategoryRecord> findBySeriesId(Long seriesId) {
        return dslContext
            .selectFrom(CATEGORY)
            .where(CATEGORY.SERIES_ID.eq(seriesId))
            .orderBy(CATEGORY.ABBREVIATION)
            .fetch();
    }

    /**
     * UC-072 (Assign athlete to series), Step 4 / BR-044: resolves the single category of
     * the series matching the athlete's gender and year of birth. Overlapping category
     * year ranges make this throw a {@code TooManyRowsException} (A3).
     */
    public Optional<Long> findIdBySeriesIdAndGenderAndYearOfBirth(long seriesId, String gender, int yearOfBirth) {
        return dslContext
            .select(CATEGORY.ID)
            .from(CATEGORY)
            .where(CATEGORY.SERIES_ID.eq(seriesId))
            .and(CATEGORY.GENDER.eq(gender))
            .and(CATEGORY.YEAR_FROM.le(yearOfBirth))
            .and(CATEGORY.YEAR_TO.ge(yearOfBirth))
            .fetchOptionalInto(Long.class);
    }

}
