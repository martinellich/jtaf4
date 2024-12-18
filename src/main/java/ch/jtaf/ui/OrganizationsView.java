package ch.jtaf.ui;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.configuration.security.Role;
import ch.jtaf.configuration.security.SecurityContext;
import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.domain.OrganizationDAO;
import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.OrganizationDialog;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import org.jooq.exception.DataAccessException;

import java.io.Serial;

import static ch.jtaf.db.tables.Organization.ORGANIZATION;

@RolesAllowed({Role.USER, Role.ADMIN})
@Route(layout = MainLayout.class)
public class OrganizationsView extends VerticalLayout implements HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient OrganizationDAO organizationDAO;
    private final transient SecurityContext securityContext;

    private Grid<OrganizationRecord> grid;
    private final OrganizationDialog dialog;

    public OrganizationsView(OrganizationDAO organizationDAO, OrganizationProvider organizationProvider,
                             SecurityContext securityContext) {
        this.organizationDAO = organizationDAO;
        this.securityContext = securityContext;

        setHeightFull();

        dialog = new OrganizationDialog(getTranslation("Organization"), organizationDAO);

        createGrid(organizationDAO, organizationProvider, securityContext);

        loadData(null);

        add(grid);
    }

    private void createGrid(OrganizationDAO organizationDAO, OrganizationProvider organizationProvider, SecurityContext securityContext) {
        var add = new Button(getTranslation("Add"));
        add.setId("add-button");
        add.addClickListener(event -> {
            var organizationRecord = ORGANIZATION.newRecord();
            organizationRecord.setOwner(securityContext.getUsername());
            dialog.open(organizationRecord, this::loadData);
        });

        grid = new Grid<>();
        grid.setId("organizations-grid");
        grid.getClassNames().add("rounded-corners");
        grid.setHeightFull();

        grid.addColumn(OrganizationRecord::getOrganizationKey).setHeader(getTranslation("Key")).setSortable(true).setAutoWidth(true).setKey(ORGANIZATION.ORGANIZATION_KEY.getName());
        grid.addColumn(OrganizationRecord::getName).setHeader(getTranslation("Name")).setSortable(true).setAutoWidth(true).setKey(ORGANIZATION.NAME.getName());

        grid.addComponentColumn(organizationRecord -> {
            var select = new Button(getTranslation("Select"));
            select.setId("select-organization-" + organizationRecord.getOrganizationKey());
            select.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            select.addClickListener(event -> {
                organizationProvider.setOrganization(organizationRecord);
                UI.getCurrent().navigate(SeriesListView.class);
            });

            var delete = new Button(getTranslation("Delete"));
            delete.setId("delete-organization-" + organizationRecord.getOrganizationKey());
            delete.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event ->
                new ConfirmDialog(
                    "delete-organization-confirm-dialog",
                    getTranslation("Confirm"),
                    getTranslation("Are.you.sure"),
                    getTranslation("Delete"), e -> {
                    try {
                        organizationDAO.deleteWithUsers(organizationRecord);

                        loadData(null);
                    } catch (DataAccessException ex) {
                        Notification.show(ex.getMessage());
                    }
                },
                    getTranslation("Cancel"), e -> {
                }).open());

            var horizontalLayout = new HorizontalLayout(select, delete);
            horizontalLayout.setJustifyContentMode(JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(add).setAutoWidth(true).setKey("edit-column");

        grid.addItemClickListener(event -> dialog.open(event.getItem(), this::loadData));
    }

    private void loadData(OrganizationRecord organizationRecord) {
        if (organizationRecord != null) {
            organizationDAO.createOrganizationUser(securityContext.getUsername(), organizationRecord);
        }

        var organizations = organizationDAO.findByUsername(securityContext.getUsername());
        grid.setItems(organizations);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Organizations");
    }
}
