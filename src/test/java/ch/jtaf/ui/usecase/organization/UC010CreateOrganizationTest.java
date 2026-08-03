package ch.jtaf.ui.usecase.organization;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.OrganizationsView;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.OrganizationDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-010: Create organization.
 * <p>
 * Also exercises UC-012 (Delete organization) — the test creates and then removes the
 * organization.
 * <p>
 * See {@code docs/use_cases/uc-010-create-organization.md} and
 * {@code docs/use_cases/uc-012-delete-organization.md}.
 */
class UC010CreateOrganizationTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
	}

	@Test
	void add_organization() {
		navigate(OrganizationsView.class);

		H1 h1 = find(H1.class).id("view-title");
		assertThat(h1.getText()).isEqualTo("Organizations");

		// Check content of organizations grid
		Grid<OrganizationRecord> organizationGrid = find(Grid.class).id("organizations-grid");
		assertThat(test(organizationGrid).size()).isEqualTo(2);
		assertThat(test(organizationGrid).getRow(0).getOrganizationKey()).isEqualTo("CIS");

		// Add organization
		gridHeaderButton(organizationGrid, "edit-column").click();
		assertThat(find(OrganizationDialog.class).all()).hasSize(1);

		test(find(TextField.class).withCaption("Key").single()).setValue("AAA");
		test(find(TextField.class).withCaption("Name").single()).setValue("Test");
		find(Button.class).withText("Save").single().click();

		// Check if organization was added
		assertThat(test(organizationGrid).size()).isEqualTo(3);
		assertThat(test(organizationGrid).getRow(2).getOrganizationKey()).isEqualTo("AAA");

		// Remove organization
		test(organizationGrid).getCellComponent(2, "edit-column")
			.getChildren()
			.filter(component -> component instanceof Button button && button.getText().equals("Delete"))
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);

		ConfirmDialog confirmDialog = find(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		find(Button.class).id("delete-organization-confirm-dialog-confirm").click();

		// Check if organization was removed
		assertThat(test(organizationGrid).size()).isEqualTo(2);
	}

}
