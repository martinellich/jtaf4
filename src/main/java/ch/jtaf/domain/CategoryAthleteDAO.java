package ch.jtaf.domain;

import ch.jtaf.db.tables.CategoryAthlete;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.CategoryAthleteRecord;
import ch.martinelli.oss.jooqspring.JooqDAO;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static org.jooq.impl.DSL.select;

@Repository
public class CategoryAthleteDAO extends JooqDAO<CategoryAthlete, CategoryAthleteRecord, CategoryAthleteId> {

    public CategoryAthleteDAO(DSLContext dslContext) {
        super(dslContext, CATEGORY_ATHLETE);
    }

    @Transactional
    public void createCategoryAthlete(AthleteRecord athleteRecord, long seriesId) {
        var categoryId = dslContext
            .select(CATEGORY.ID)
            .from(CATEGORY)
            .where(CATEGORY.SERIES_ID.eq(seriesId))
            .and(CATEGORY.GENDER.eq(athleteRecord.getGender()))
            .and(CATEGORY.YEAR_FROM.le(athleteRecord.getYearOfBirth()))
            .and(CATEGORY.YEAR_TO.ge(athleteRecord.getYearOfBirth()))
            .fetchOneInto(Long.class);

        var categoryAthleteRecord = CATEGORY_ATHLETE.newRecord();
        categoryAthleteRecord.setAthleteId(athleteRecord.getId());
        categoryAthleteRecord.setCategoryId(categoryId);
        categoryAthleteRecord.attach(dslContext.configuration());
        categoryAthleteRecord.store();
    }

    @Transactional
    public void deleteCategoryAthlete(AthleteRecord athleteRecord, long seriesId) {
        dslContext
            .deleteFrom(CATEGORY_ATHLETE)
            .where(CATEGORY_ATHLETE.ATHLETE_ID.eq(athleteRecord.getId()))
            .and(CATEGORY_ATHLETE.CATEGORY_ID.in(
                select(CATEGORY.ID).from(CATEGORY).where(CATEGORY.SERIES_ID.eq(seriesId))))
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
        int updatedRows = dslContext.update(CATEGORY_ATHLETE)
            .set(CATEGORY_ATHLETE.DNF, dnf)
            .where(CATEGORY_ATHLETE.ATHLETE_ID.eq(athleteId))
            .and(CATEGORY_ATHLETE.CATEGORY_ID.eq(categoryId))
            .execute();
        if (updatedRows != 1) {
            throw new IllegalStateException("Dnf update failed");
        }
    }
}
