package ch.jtaf.domain;

import ch.jtaf.db.tables.Organization;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.db.tables.records.OrganizationUserRecord;
import ch.martinelli.oss.jooqspring.JooqDAO;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ch.jtaf.db.tables.Organization.ORGANIZATION;
import static ch.jtaf.db.tables.OrganizationUser.ORGANIZATION_USER;
import static ch.jtaf.db.tables.SecurityUser.SECURITY_USER;

@Repository
public class OrganizationDAO extends JooqDAO<Organization, OrganizationRecord, Long> {

    public OrganizationDAO(DSLContext dslContext) {
        super(dslContext, ORGANIZATION);
    }

    @Transactional
    public void deleteWithUsers(OrganizationRecord organizationRecord) {
        dslContext
            .deleteFrom(ORGANIZATION_USER)
            .where(ORGANIZATION_USER.ORGANIZATION_ID.eq(organizationRecord.getId()))
            .execute();
        delete(organizationRecord);
    }

    @Transactional
    public void createOrganizationUser(String username, OrganizationRecord organizationRecord) {
        dslContext.selectFrom(SECURITY_USER)
            .where(SECURITY_USER.EMAIL.eq(username))
            .fetchOptional()
            .ifPresent(user -> {
                var organizationUser = new OrganizationUserRecord();
                organizationUser.setOrganizationId(organizationRecord.getId());
                organizationUser.setUserId(user.getId());
                dslContext.attach(organizationUser);
                organizationUser.store();
            });
    }

    public List<OrganizationRecord> findByUsername(String username) {
        return dslContext
            .select(ORGANIZATION_USER.organization().fields())
            .from(ORGANIZATION_USER)
            .where(ORGANIZATION_USER.securityUser().EMAIL.eq(username))
            .fetch().into(ORGANIZATION);
    }
}
