package ch.jtaf.domain;

import ch.jtaf.domain.data.CategoriesData;
import ch.jtaf.domain.report.CategoriesReport;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

// @formatter:off
@Service
public class CategoriesReportService {

    private final DSLContext dslContext;

    public CategoriesReportService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public byte[] createCategoriesSheet(Long seriesId, Locale locale) {
        return new CategoriesReport(getCategories(seriesId).orElseThrow(), locale).create();
    }

    private Optional<CategoriesData> getCategories(Long seriesId) {
        return dslContext
            .select(SERIES.NAME,
                    multiset(
                        select(CATEGORY.ABBREVIATION, CATEGORY.NAME, CATEGORY.GENDER, CATEGORY.YEAR_FROM, CATEGORY.YEAR_TO,
                               multiset(
                                   select(CATEGORY_EVENT.event().NAME)
                                       .from(CATEGORY_EVENT)
                                       .where(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
                                       .orderBy(CATEGORY_EVENT.POSITION))
                                   .convertFrom(r -> r.map(record -> record.value1())))
                            .from(CATEGORY)
                            .where(CATEGORY.SERIES_ID.eq(SERIES.ID))
                            .orderBy(CATEGORY.ABBREVIATION))
                        .convertFrom(r -> r.map(mapping(CategoriesData.Category::new))))
            .from(SERIES)
            .where(SERIES.ID.eq(seriesId))
            .fetchOptional(mapping(CategoriesData::new));
    }

}
