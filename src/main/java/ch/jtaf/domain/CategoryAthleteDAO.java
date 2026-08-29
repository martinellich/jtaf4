package ch.jtaf.domain;

import ch.jtaf.db.tables.CategoryAthlete;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.CategoryAthleteRecord;
import ch.martinelli.oss.jooqspring.JooqDAO;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static org.jooq.impl.DSL.select;

// @formatter:off
@Repository
public class CategoryAthleteDAO extends JooqDAO<CategoryAthlete, CategoryAthleteRecord, CategoryAthleteId> {

    private final CategoryDAO categoryDAO;

    public CategoryAthleteDAO(DSLContext dslContext, CategoryDAO categoryDAO) {
        super(dslContext, CATEGORY_ATHLETE);
        this.categoryDAO = categoryDAO;
    }

    /**
     * Assigns the athlete to the category of the series that matches the athlete's gender
     * and year of birth.
     * @return the id of the matching category, or empty if the series has no category for
     * the athlete (in this case nothing is inserted)
     */
    @Transactional
    public Optional<Long> createCategoryAthlete(AthleteRecord athleteRecord, long seriesId) {
        var categoryId = categoryDAO
            .findIdBySeriesIdAndGenderAndYearOfBirth(seriesId, athleteRecord.getGender(), athleteRecord.getYearOfBirth());

        if (categoryId.isPresent() && !isAssignedToSeries(athleteRecord.getId(), seriesId)) {
            createCategoryAthlete(athleteRecord.getId(), categoryId.get());
        }
        return categoryId;
    }

    @Transactional
    public void createCategoryAthlete(Long athleteId, @Nullable Long categoryId) {
        var categoryAthleteRecord = CATEGORY_ATHLETE.newRecord();
        categoryAthleteRecord.setAthleteId(athleteId);
        categoryAthleteRecord.setCategoryId(categoryId);
        categoryAthleteRecord.attach(dslContext.configuration());
        categoryAthleteRecord.store();
    }

    public boolean isAssignedToSeries(Long athleteId, long seriesId) {
        return dslContext
            .fetchExists(dslContext
                .selectFrom(CATEGORY_ATHLETE)
                .where(CATEGORY_ATHLETE.ATHLETE_ID.eq(athleteId))
                .and(CATEGORY_ATHLETE.CATEGORY_ID.in(select(CATEGORY.ID).from(CATEGORY).where(CATEGORY.SERIES_ID.eq(seriesId)))));
    }

    @Transactional
    public void deleteCategoryAthlete(AthleteRecord athleteRecord, long seriesId) {
        dslContext
            .deleteFrom(CATEGORY_ATHLETE)
            .where(CATEGORY_ATHLETE.ATHLETE_ID.eq(athleteRecord.getId()))
            .and(CATEGORY_ATHLETE.CATEGORY_ID.in(select(CATEGORY.ID).from(CATEGORY).where(CATEGORY.SERIES_ID.eq(seriesId))))
            .execute();
    }

    public int countAthletesBySeriesId(Long seriesId) {
        return dslContext
            .select(DSL.count(CATEGORY_ATHLETE.ATHLETE_ID)).from(CATEGORY_ATHLETE)
            .where(CATEGORY_ATHLETE.category().SERIES_ID.eq(seriesId))
            .fetchOptionalInto(Integer.class).orElse(0);
    }

    @Transactional
    public void setDnf(Long athleteId, Long categoryId, boolean dnf) {
        int updatedRows = dslContext
            .update(CATEGORY_ATHLETE)
            .set(CATEGORY_ATHLETE.DNF, dnf)
            .where(CATEGORY_ATHLETE.ATHLETE_ID.eq(athleteId))
            .and(CATEGORY_ATHLETE.CATEGORY_ID.eq(categoryId))
            .execute();
        if (updatedRows != 1) {
            throw new IllegalStateException("Dnf update failed");
        }
    }
}
