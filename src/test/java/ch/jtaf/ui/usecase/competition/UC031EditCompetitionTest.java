package ch.jtaf.ui.usecase.competition;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CompetitionRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.dialog.CompetitionDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-031: Edit competition.
 * <p>
 * See {@code docs/use_cases/uc-031-edit-competition.md}.
 */
class UC031EditCompetitionTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));

		Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
		test(seriesGrid).clickRow(0);
	}

	@Test
	void edit_competition() {
		Grid<CompetitionRecord> competitionsGrid = $(Grid.class).id("competitions-grid");
		assertThat(test(competitionsGrid).size()).isEqualTo(2);
		assertThat(test(competitionsGrid).getRow(0).getName()).isEqualTo("1. CIS Twann");

		test(competitionsGrid).clickRow(0);
		assertThat($(CompetitionDialog.class).all()).hasSize(1);

		TextField name = $(TextField.class).withCaption("Name").withValue("1. CIS Twann").single();

		test(name).setValue("1. CIS Twann Renamed");
		$(Button.class).id("edit-save").click();

		assertThat(test(competitionsGrid).getRow(0).getName()).isEqualTo("1. CIS Twann Renamed");

		// Restore original name
		test(competitionsGrid).clickRow(0);
		test($(TextField.class).withCaption("Name").withValue("1. CIS Twann Renamed").single())
			.setValue("1. CIS Twann");
		$(Button.class).id("edit-save").click();

		assertThat(test(competitionsGrid).getRow(0).getName()).isEqualTo("1. CIS Twann");
	}

}
