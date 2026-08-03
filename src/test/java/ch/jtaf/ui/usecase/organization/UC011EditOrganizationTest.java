package ch.jtaf.ui.usecase.organization;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.OrganizationsView;
import ch.jtaf.ui.dialog.OrganizationDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-011: Edit organization.
 * <p>
 * See {@code docs/use_cases/uc-011-edit-organization.md}.
 */
class UC011EditOrganizationTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
		navigate(OrganizationsView.class);
	}

	@Test
	void edit_organization() {
		Grid<OrganizationRecord> organizationGrid = find(Grid.class).id("organizations-grid");
		String originalName = test(organizationGrid).getRow(0).getName();

		test(organizationGrid).clickRow(0);

		assertThat(find(OrganizationDialog.class).all()).hasSize(1);

		TextField name = find(TextField.class).id("name");
		assertThat(name.getValue()).isEqualTo(originalName);

		test(name).setValue(originalName + " Renamed");
		find(Button.class).withText("Save").single().click();

		assertThat(test(organizationGrid).getRow(0).getName()).isEqualTo(originalName + " Renamed");

		// Restore original value
		test(organizationGrid).clickRow(0);
		test(find(TextField.class).id("name")).setValue(originalName);
		find(Button.class).withText("Save").single().click();

		assertThat(test(organizationGrid).getRow(0).getName()).isEqualTo(originalName);
	}

}
