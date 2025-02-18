package ch.jtaf.domain;

import ch.jtaf.reporting.data.ClubRankingData;
import ch.jtaf.reporting.data.SeriesRankingData;
import ch.jtaf.reporting.report.ClubRankingReport;
import ch.jtaf.reporting.report.SeriesRankingReport;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Result.RESULT;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.*;

// @formatter:off
@Service
public class SeriesRankingService {

    private final DSLContext dslContext;

    public SeriesRankingService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public byte[] getSeriesRankingAsPdf(Long seriesId, Locale locale) {
        return new SeriesRankingReport(getSeriesRanking(seriesId).orElseThrow(), locale).create();
    }

    public Optional<SeriesRankingData> getSeriesRanking(Long seriesId) {
        return dslContext
            .select(
                COMPETITION.series().NAME,
                count(COMPETITION.ID),
                multiset(
                    select(
                        CATEGORY.ABBREVIATION,
                        CATEGORY.NAME,
                        CATEGORY.YEAR_FROM,
                        CATEGORY.YEAR_TO,
                        multiset(
                            select(
                                CATEGORY_ATHLETE.athlete().FIRST_NAME,
                                CATEGORY_ATHLETE.athlete().LAST_NAME,
                                CATEGORY_ATHLETE.athlete().YEAR_OF_BIRTH,
                                CATEGORY_ATHLETE.athlete().club().NAME,
                                multiset(
                                    select(
                                        RESULT.competition().NAME,
                                        sum(RESULT.POINTS)
                                    )
                                        .from(RESULT)
                                        .where(RESULT.ATHLETE_ID.eq(CATEGORY_ATHLETE.ATHLETE_ID))
                                        .and(RESULT.competition().SERIES_ID.eq(COMPETITION.series().ID))
                                        .groupBy(RESULT.competition().NAME, RESULT.competition().COMPETITION_DATE)
                                        .orderBy(RESULT.competition().COMPETITION_DATE)
                                ).convertFrom(r -> r.map(mapping(SeriesRankingData.Category.Athlete.Result::new)))
                            )
                                .from(CATEGORY_ATHLETE)
                                .where(CATEGORY_ATHLETE.CATEGORY_ID.eq(CATEGORY.ID))
                                .and(CATEGORY_ATHLETE.DNF.eq(false))
                        ).convertFrom(r -> r.map(mapping(SeriesRankingData.Category.Athlete::new)))
                    )
                        .from(CATEGORY)
                        .where(CATEGORY.SERIES_ID.eq(COMPETITION.series().ID))
                        .orderBy(CATEGORY.ABBREVIATION)
                ).convertFrom(r -> r.map(mapping(SeriesRankingData.Category::new)))
            )
            .from(COMPETITION)
            .where(COMPETITION.SERIES_ID.eq(seriesId))
            .groupBy(COMPETITION.series().ID, COMPETITION.series().NAME)
            .fetchOptional(mapping(SeriesRankingData::new));
    }

    public byte[] getClubRankingAsPdf(Long seriesId, Locale locale) {
        return new ClubRankingReport(getClubRanking(seriesId).orElseThrow(), locale).create();
    }

    public Optional<ClubRankingData> getClubRanking(Long seriesId) {
        return dslContext
            .select(
                SERIES.NAME,
                multiset(
                    select(
                        RESULT.athlete().club().NAME,
                        sum(RESULT.POINTS)
                    )
                        .from(RESULT)
                        .where(RESULT.competition().SERIES_ID.eq(seriesId))
                        .groupBy(RESULT.athlete().club().NAME)
                ).convertFrom(r -> r.map(mapping(ClubRankingData.Result::new)))
            )
            .from(SERIES)
            .where(SERIES.ID.eq(seriesId))
            .fetchOptional(mapping(ClubRankingData::new));
    }
}
