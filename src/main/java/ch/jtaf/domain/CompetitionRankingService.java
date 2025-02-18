package ch.jtaf.domain;

import ch.jtaf.reporting.data.CompetitionRankingData;
import ch.jtaf.reporting.data.EventsRankingData;
import ch.jtaf.reporting.report.CompetitionRankingReport;
import ch.jtaf.reporting.report.DiplomaReport;
import ch.jtaf.reporting.report.EventsRankingReport;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Event.EVENT;
import static ch.jtaf.db.tables.Result.RESULT;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.*;

// @formatter:off
@Service
public class CompetitionRankingService {

    private final DSLContext dslContext;

    public CompetitionRankingService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public byte[] getCompetitionRankingAsPdf(Long competitionId, Locale locale) {
        return new CompetitionRankingReport(getCompetitionRanking(competitionId).orElseThrow(), locale).create();
    }

    public byte[] getDiplomasAsPdf(Long competitionId, Locale locale) {
        return new DiplomaReport(getCompetitionRanking(competitionId).orElseThrow(), getLogo(competitionId), locale).create();
    }

    public byte[] getEventRankingAsPdf(Long competitionId, Locale locale) {
        return new EventsRankingReport(getEventsRanking(competitionId).orElseThrow(), locale).create();
    }

    public Optional<CompetitionRankingData> getCompetitionRanking(Long competitionId) {
        return dslContext
            .select(
                COMPETITION.NAME,
                COMPETITION.COMPETITION_DATE,
                COMPETITION.ALWAYS_FIRST_THREE_MEDALS,
                COMPETITION.MEDAL_PERCENTAGE,
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
                                CATEGORY_ATHLETE.DNF,
                                multiset(
                                    select(
                                        RESULT.event().ABBREVIATION,
                                        RESULT.RESULT_,
                                        RESULT.POINTS
                                    )
                                        .from(RESULT)
                                        .where(RESULT.ATHLETE_ID.eq(CATEGORY_ATHLETE.athlete().ID))
                                        .and(RESULT.COMPETITION_ID.eq(COMPETITION.ID))
                                        .and(RESULT.CATEGORY_ID.eq(CATEGORY.ID))
                                        .orderBy(RESULT.POSITION)
                                ).convertFrom(r -> r.map(mapping(CompetitionRankingData.Category.Athlete.Result::new)))
                            )
                                .from(CATEGORY_ATHLETE)
                                .where(CATEGORY_ATHLETE.CATEGORY_ID.eq(CATEGORY.ID))
                        ).convertFrom(r -> r.map(mapping(CompetitionRankingData.Category.Athlete::new)))
                    )
                        .from(CATEGORY)
                        .where(CATEGORY.SERIES_ID.eq(COMPETITION.SERIES_ID))
                        .orderBy(CATEGORY.ABBREVIATION)
                ).convertFrom(r -> r.map(mapping(CompetitionRankingData.Category::new)))
            )
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOptional(mapping(CompetitionRankingData::new));
    }

    public Optional<EventsRankingData> getEventsRanking(Long competitionId) {
        return dslContext
            .select(
                COMPETITION.NAME,
                COMPETITION.COMPETITION_DATE,
                multiset(
                    select(
                        EVENT.ABBREVIATION,
                        EVENT.GENDER,
                        EVENT.EVENT_TYPE,
                        multiset(
                            selectDistinct(
                                RESULT.athlete().LAST_NAME,
                                RESULT.athlete().FIRST_NAME,
                                RESULT.athlete().YEAR_OF_BIRTH,
                                RESULT.category().ABBREVIATION,
                                RESULT.athlete().club().NAME,
                                RESULT.RESULT_)
                                .from(RESULT)
                                .where(RESULT.category().SERIES_ID.eq(COMPETITION.SERIES_ID))
                                .and(RESULT.event().ID.eq(EVENT.ID))
                                .and(RESULT.RESULT_.isNotNull())
                        ).convertFrom(r -> r.map(mapping(EventsRankingData.Event.Result::new))))
                        .from(EVENT)
                        .where(EVENT.ORGANIZATION_ID.eq(COMPETITION.series().ORGANIZATION_ID))
                        .orderBy(EVENT.ABBREVIATION, EVENT.GENDER))
                    .convertFrom(r -> r.map(mapping(EventsRankingData.Event::new))))
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOptional(mapping(EventsRankingData::new));
    }

    private byte[] getLogo(Long competitionId) {
        var logoRecord = dslContext
            .select(COMPETITION.series().LOGO)
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOne();
        return logoRecord != null ? logoRecord.get(SERIES.LOGO) : new byte[0];
    }
}
