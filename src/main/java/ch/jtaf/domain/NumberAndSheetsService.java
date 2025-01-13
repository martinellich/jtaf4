package ch.jtaf.domain;

import ch.jtaf.reporting.data.NumbersAndSheetsAthlete;
import ch.jtaf.reporting.data.NumbersAndSheetsCompetition;
import ch.jtaf.reporting.report.NumbersReport;
import ch.jtaf.reporting.report.SheetsReport;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.CategoryAthlete.CATEGORY_ATHLETE;
import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Series.SERIES;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.multiset;
import static org.jooq.impl.DSL.select;

@Service
public class NumberAndSheetsService {

    private final DSLContext dslContext;

    public NumberAndSheetsService(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public byte[] createNumbers(Long seriesId, Locale locale, Field<?>... orderBy) {
        return new NumbersReport(getAthletes(seriesId, orderBy), locale).create();
    }

    public byte[] createSheets(Long seriesId, Long competitionId, Locale locale, Field<?>... orderBy) {
        return new SheetsReport(getCompetition(competitionId).orElseThrow(), getAthletes(seriesId, orderBy),
                getLogo(seriesId), locale)
            .create();
    }

    public byte[] createEmptySheets(Long seriesId, Long categoryId, Locale locale) {
        return new SheetsReport(createDummyAthlete(categoryId).orElseThrow(), getLogo(seriesId), locale).create();
    }

    private Optional<NumbersAndSheetsAthlete> createDummyAthlete(Long categoryId) {
        return dslContext
            .select(DSL.inline(null, SQLDataType.BIGINT), DSL.inline(null, SQLDataType.VARCHAR),
                    DSL.inline(null, SQLDataType.VARCHAR), DSL.inline(null, SQLDataType.INTEGER), CATEGORY.ABBREVIATION,
                    CATEGORY.NAME, DSL.inline(null, SQLDataType.VARCHAR),
                    multiset(select(CATEGORY_EVENT.event().NAME, CATEGORY_EVENT.event().EVENT_TYPE).from(CATEGORY_EVENT)
                        .where(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY.ID))
                        .orderBy(CATEGORY_EVENT.POSITION))
                        .convertFrom(r -> r.map(mapping(NumbersAndSheetsAthlete.Event::new))))
            .from(CATEGORY)
            .where(CATEGORY.ID.eq(categoryId))
            .fetchOptional(mapping(NumbersAndSheetsAthlete::new));
    }

    private Optional<NumbersAndSheetsCompetition> getCompetition(Long competitionId) {
        return dslContext.select(COMPETITION.NAME, COMPETITION.COMPETITION_DATE)
            .from(COMPETITION)
            .where(COMPETITION.ID.eq(competitionId))
            .fetchOptionalInto(NumbersAndSheetsCompetition.class);
    }

    private byte[] getLogo(Long id) {
        var logoRecord = dslContext.select(SERIES.LOGO).from(SERIES).where(SERIES.ID.eq(id)).fetchOne();
        if (logoRecord != null) {
            return logoRecord.get(SERIES.LOGO);
        }
        else {
            return new byte[0];
        }
    }

    private List<NumbersAndSheetsAthlete> getAthletes(Long seriesId, Field<?>... orderBy) {
        return dslContext
            .select(ATHLETE.ID, ATHLETE.FIRST_NAME, ATHLETE.LAST_NAME, ATHLETE.YEAR_OF_BIRTH, CATEGORY.ABBREVIATION,
                    CATEGORY.NAME, CLUB.ABBREVIATION,
                    multiset(select(CATEGORY_EVENT.event().NAME, CATEGORY_EVENT.event().EVENT_TYPE).from(CATEGORY_EVENT)
                        .where(CATEGORY_EVENT.CATEGORY_ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
                        .orderBy(CATEGORY_EVENT.POSITION))
                        .convertFrom(r -> r.map(mapping(NumbersAndSheetsAthlete.Event::new))))
            .from(CATEGORY_ATHLETE)
            .join(ATHLETE)
            .on(ATHLETE.ID.eq(CATEGORY_ATHLETE.ATHLETE_ID))
            .join(CATEGORY)
            .on(CATEGORY.ID.eq(CATEGORY_ATHLETE.CATEGORY_ID))
            .leftOuterJoin(CLUB)
            .on(CLUB.ID.eq(ATHLETE.CLUB_ID))
            .where(CATEGORY.series().ID.eq(seriesId))
            .orderBy(orderBy)
            .fetch(mapping(NumbersAndSheetsAthlete::new));
    }

}
