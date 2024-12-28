package ch.jtaf.domain;

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.predicates.SkipFlywayTablesPredicate;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
class DatabaseStructureStaticAnalysisTest {

    @MockitoBean
    private JavaMailSender javaMailSender;

    @Autowired
    private List<DatabaseCheckOnHost<? extends DbObject>> checks;

    @Test
    void checksShouldWork() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .filter(c -> c.getDiagnostic() != Diagnostic.TABLES_WITHOUT_DESCRIPTION &&
                c.getDiagnostic() != Diagnostic.COLUMNS_WITHOUT_DESCRIPTION) // TODO Do we need a documentation for database objects?
            .forEach(c -> {
                final ListAssert<? extends DbObject> checkAssert = assertThat(c.check(SkipFlywayTablesPredicate.ofPublic()))
                    .as(c.getDiagnostic().name());

                if (c.getDiagnostic() == Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX) {
                    // https://www.postgresql.org/docs/current/ddl-constraints.html#DDL-CONSTRAINTS-FK
                    // The declaration of a foreign key constraint does not automatically create an index on the referencing columns,
                    // but it is often a good idea to index the referencing columns too.
                    checkAssert
                        .hasSize(15)
                        .asInstanceOf(list(ForeignKey.class))
                        .containsExactly(
                            ForeignKey.ofNullableColumn("athlete", "fk_athlete_club", "club_id"),
                            ForeignKey.ofNullableColumn("athlete", "fk_athlete_organization", "organization_id"),
                            ForeignKey.ofNullableColumn("category", "fk_category_series", "series_id"),
                            ForeignKey.ofNotNullColumn("category_athlete", "fk_category_athlete_category", "category_id"),
                            ForeignKey.ofNotNullColumn("category_event", "fk_category_event_event", "event_id"),
                            ForeignKey.ofNullableColumn("club", "fk_club_organization", "organization_id"),
                            ForeignKey.ofNullableColumn("competition", "fk_competition_series", "series_id"),
                            ForeignKey.ofNullableColumn("event", "fk_event_organization", "organization_id"),
                            ForeignKey.ofNotNullColumn("organization_user", "fk_organization_user_user", "user_id"),
                            ForeignKey.ofNotNullColumn("result", "fk_result_athlete", "athlete_id"),
                            ForeignKey.ofNotNullColumn("result", "fk_result_category", "category_id"),
                            ForeignKey.ofNotNullColumn("result", "fk_result_competition", "competition_id"),
                            ForeignKey.ofNotNullColumn("result", "fk_result_event", "event_id"),
                            ForeignKey.ofNullableColumn("series", "fk_series_organization", "organization_id"),
                            ForeignKey.ofNotNullColumn("user_group", "fk_user_group_user", "user_id")
                        );
                } else {
                    // The list of detected deviations should be empty
                    checkAssert.isEmpty();
                }
            });
    }
}
