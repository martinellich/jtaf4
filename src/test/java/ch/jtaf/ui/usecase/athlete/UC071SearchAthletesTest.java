package ch.jtaf.ui.usecase.athlete;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.AthletesView;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-071: Search athletes.
 * <p>
 * See {@code docs/use_cases/uc-071-search-athletes.md}.
 */
class UC071SearchAthletesTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigateToSeriesList();
		navigate(AthletesView.class);
	}

	@Test
	void filter_and_sort() {
		// Check number of athletes before filtering
		Grid<AthleteRecord> athletesGrid = $(Grid.class).id("athletes-grid");
		assertThat(test(athletesGrid).size()).isEqualTo(140);

		// Filter
		test($(TextField.class).id("filter")).setValue("Martinelli");
		assertThat(test(athletesGrid).size()).isEqualTo(1);

		// Remove filter
		test($(TextField.class).id("filter")).setValue("");
		assertThat(test(athletesGrid).size()).isEqualTo(140);

		// Sort grid
		test(athletesGrid).sortByColumn(ATHLETE.LAST_NAME.getName(), SortDirection.ASCENDING);
		assertThat(test(athletesGrid).getRow(0).getLastName()).isEqualTo("Abaterusso");

		test(athletesGrid).sortByColumn(ATHLETE.LAST_NAME.getName(), SortDirection.DESCENDING);
		assertThat(test(athletesGrid).getRow(0).getLastName()).isEqualTo("Zumstein");
	}

}
