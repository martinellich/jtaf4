package ch.jtaf.domain;

import ch.jtaf.db.tables.Series;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.martinelli.oss.jooqspring.JooqDAO;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;

@Repository
public class SeriesDAO extends JooqDAO<Series, SeriesRecord, Long> {

    public SeriesDAO(DSLContext dslContext) {
        super(dslContext, SERIES);
    }

    @Transactional
    public void copyCategories(Long seriesIdToCopy, Long currentSeriesId) {
        dslContext.selectFrom(CATEGORY)
            .where(CATEGORY.SERIES_ID.eq(seriesIdToCopy))
            .fetch()
            .forEach(category -> {
                var copyCategory = category.copy();
                copyCategory.setSeriesId(currentSeriesId);
                dslContext.attach(copyCategory);
                copyCategory.store();
                dslContext.selectFrom(CATEGORY_EVENT)
                    .where(CATEGORY_EVENT.CATEGORY_ID.eq(category.getId()))
                    .fetch()
                    .forEach(categoryEvent -> {
                        var copyCategoryEvent = categoryEvent.copy();
                        copyCategoryEvent.setCategoryId(copyCategory.getId());
                        copyCategoryEvent.setEventId(categoryEvent.getEventId());
                        dslContext.attach(copyCategoryEvent);
                        copyCategoryEvent.store();
                    });
            });
    }

    @Transactional
    public void deleteSeries(long seriesId) {
        dslContext.deleteFrom(CATEGORY_EVENT)
            .where(CATEGORY_EVENT.category().SERIES_ID.eq(seriesId))
            .execute();

        dslContext.deleteFrom(CATEGORY)
            .where(CATEGORY.SERIES_ID.eq(seriesId))
            .execute();

        dslContext.deleteFrom(SERIES)
            .where(SERIES.ID.eq(seriesId))
            .execute();
    }

    public List<SeriesRecord> findByOrganizationIdAndSeriesId(long organizationId, long seriesId, int offset, int limit) {
        return dslContext
            .selectFrom(SERIES)
            .where(SERIES.ORGANIZATION_ID.eq(organizationId))
            .and(SERIES.ID.ne(seriesId))
            .orderBy(SERIES.NAME)
            .offset(offset).limit(limit)
            .fetch();
    }

    public List<SeriesRecord> findAllOrderByCompetitionDate() {
        return dslContext
            .selectFrom(SERIES)
            .orderBy(field(select(DSL.max(COMPETITION.COMPETITION_DATE)).from(COMPETITION).where(COMPETITION.SERIES_ID.eq(SERIES.ID))).desc())
            .fetch();
    }
}
