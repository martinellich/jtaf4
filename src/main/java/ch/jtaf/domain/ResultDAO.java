package ch.jtaf.domain;

import ch.jtaf.db.tables.Result;
import ch.jtaf.db.tables.records.ResultRecord;
import ch.martinelli.oss.jooqspring.JooqDAO;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static ch.jtaf.db.tables.Result.RESULT;

@Repository
public class ResultDAO extends JooqDAO<Result, ResultRecord, Long> {

    public ResultDAO(DSLContext dslContext) {
        super(dslContext, RESULT);
    }

    public Optional<ResultRecord> getResults(long competitionId, long athleteId, long categoryId, long eventId) {
        return dslContext
            .selectFrom(RESULT)
            .where(RESULT.COMPETITION_ID.eq(competitionId))
            .and(RESULT.ATHLETE_ID.eq(athleteId))
            .and(RESULT.CATEGORY_ID.eq(categoryId))
            .and(RESULT.EVENT_ID.eq(eventId))
            .fetchOptional();
    }
}
