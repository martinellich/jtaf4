package ch.jtaf.ui.usecase.organization;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.ui.AbstractViewTest;
import ch.jtaf.ui.OrganizationsView;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.OrganizationDialog;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UC-012: Delete organization.
 * <p>
 * See {@code docs/use_cases/uc-012-delete-organization.md}.
 */
class UC012DeleteOrganizationTest extends AbstractViewTest {

	@BeforeEach
	void login() {
		login("simon@martinelli.ch", "", List.of(Role.ADMIN));
	}

	@Test
	void delete_organization() {
		navigate(OrganizationsView.class);

		// Add an organization that is safe to delete (no series, competitions, etc.).
		Grid<OrganizationRecord> organizationGrid = $(Grid.class).id("organizations-grid");
		gridHeaderButton(organizationGrid, "edit-column").click();
		assertThat($(OrganizationDialog.class).all()).hasSize(1);

		test($(TextField.class).withCaption("Key").single()).setValue("DEL");
		test($(TextField.class).withCaption("Name").single()).setValue("Disposable");
		$(Button.class).withText("Save").single().click();

		assertThat(test(organizationGrid).size()).isEqualTo(3);
		int deleteRow = 2;
		assertThat(test(organizationGrid).getRow(deleteRow).getOrganizationKey()).isEqualTo("DEL");

		// A2: user cancels — confirmation dialog closes and the organization stays.
		clickDeleteOnRow(organizationGrid, deleteRow);
		ConfirmDialog confirmDialog = $(ConfirmDialog.class).single();
		assertThat(confirmDialog.isOpened()).isTrue();
		$(Button.class).id("delete-organization-confirm-dialog-cancel").click();

		assertThat($(ConfirmDialog.class).all()).isEmpty();
		assertThat(test(organizationGrid).size()).isEqualTo(3);

		// Main flow: user confirms — organization and membership links are removed.
		clickDeleteOnRow(organizationGrid, deleteRow);
		assertThat($(ConfirmDialog.class).single().isOpened()).isTrue();
		$(Button.class).id("delete-organization-confirm-dialog-confirm").click();

		assertThat(test(organizationGrid).size()).isEqualTo(2);
		assertThat(test(organizationGrid).getRow(0).getOrganizationKey()).isEqualTo("CIS");
		assertThat(test(organizationGrid).getRow(1).getOrganizationKey()).isEqualTo("TVE");
	}

	private void clickDeleteOnRow(Grid<OrganizationRecord> organizationGrid, int row) {
		test(organizationGrid).getCellComponent(row, "edit-column")
			.getChildren()
			.filter(component -> component instanceof Button button && button.getText().equals("Delete"))
			.findFirst()
			.map(Button.class::cast)
			.ifPresent(Button::click);
	}

}
