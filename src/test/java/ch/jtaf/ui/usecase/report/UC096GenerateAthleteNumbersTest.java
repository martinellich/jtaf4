package ch.jtaf.ui.usecase.report;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * UC-096: Generate athlete numbers.
 * <p>
 * See {@code docs/use_cases/uc-096-generate-athlete-numbers.md}.
 */
@SuppressWarnings("java:S2699")
class UC096GenerateAthleteNumbersTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);
	}

	@Test
	void numbers_ordered_by_athlete() {
		Grid<CompetitionRecord> competitionsGrid = $(Grid.class).id("competitions-grid");
		Anchor numbers = test(competitionsGrid).getCellComponent(0, 2)
			.getChildren()
			.filter(Anchor.class::isInstance)
			.map(Anchor.class::cast)
			.filter(anchor -> "Numbers".equals(anchor.getText()))
			.findFirst()
			.orElseThrow();
		assertThatNoException().isThrownBy(() -> test(numbers).download(new ByteArrayOutputStream()));
	}

}
