package ch.jtaf.domain;

import ch.jtaf.db.tables.Event;
import ch.jtaf.db.tables.records.EventRecord;
import ch.martinelli.oss.jooqspring.JooqDAO;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;
import static ch.jtaf.db.tables.Event.EVENT;

@Repository
public class EventDAO extends JooqDAO<Event, EventRecord, Long> {

    public EventDAO(DSLContext dslContext) {
        super(dslContext, EVENT);
    }

    public List<EventRecord> findAllByOrganizationGenderCategory(long organizationId, String gender, long categoryId,
                                                                 Condition condition, int offset, int limit) {
        return dslContext
            .selectFrom(EVENT)
            .where(EVENT.ORGANIZATION_ID.eq(organizationId))
            .and(EVENT.GENDER.eq(gender)
                .and(EVENT.ID.notIn(DSL
                    .select(CATEGORY_EVENT.EVENT_ID)
                    .from(CATEGORY_EVENT)
                    .where(CATEGORY_EVENT.CATEGORY_ID.eq(categoryId))
                ))
                .and(condition))
            .orderBy(EVENT.ABBREVIATION, EVENT.GENDER)
            .offset(offset).limit(limit)
            .fetch();
    }

    public int countByOrganizationGenderCategory(long organizationId, String gender, long categoryId, Condition condition) {
        return dslContext
            .selectCount()
            .from(EVENT)
            .where(EVENT.ORGANIZATION_ID.eq(organizationId))
            .and(EVENT.GENDER.eq(gender))
            .and(EVENT.ID.notIn(dslContext
                .select(CATEGORY_EVENT.EVENT_ID)
                .from(CATEGORY_EVENT)
                .where(CATEGORY_EVENT.CATEGORY_ID.eq(categoryId))
            ))
            .and(condition)
            .fetchOptionalInto(Integer.class).orElse(0);
    }

    public List<EventRecord> findByCategoryIdOrderByPosition(Long categoryId) {
        return dslContext
            .select(CATEGORY_EVENT.event().fields())
            .from(CATEGORY_EVENT)
            .where(CATEGORY_EVENT.CATEGORY_ID.eq(categoryId))
            .orderBy(CATEGORY_EVENT.POSITION)
            .fetchInto(EventRecord.class);
    }
}
