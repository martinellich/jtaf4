package ch.jtaf.ui;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.domain.ClubDAO;
import ch.jtaf.ui.dialog.ClubDialog;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.OrderField;

import java.io.Serial;
import java.util.List;

import static ch.jtaf.db.tables.Club.CLUB;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@Route(layout = MainLayout.class)
public class ClubsView extends ProtectedGridView<ClubRecord> {

    @Serial
    private static final long serialVersionUID = 1L;
    private final ClubDialog dialog;

    public ClubsView(ClubDAO clubDAO, OrganizationProvider organizationProvider) {
        super(clubDAO, organizationProvider, CLUB);

        setHeightFull();

        dialog = new ClubDialog(getTranslation("Clubs"), clubDAO);

        createGrid();

        add(grid);
    }

    private void createGrid() {
        grid.setId("clubs-grid");

        grid.addColumn(ClubRecord::getAbbreviation).setHeader(getTranslation("Abbreviation")).setSortable(true).setAutoWidth(true).setKey(CLUB.ABBREVIATION.getName());
        grid.addColumn(ClubRecord::getName).setHeader(getTranslation("Name")).setSortable(true).setAutoWidth(true).setKey(CLUB.NAME.getName());

        addActionColumnAndSetSelectionListener(JooqDAO, grid, dialog, clubRecord -> refreshAll(),
            () -> {
                ClubRecord newRecord = CLUB.newRecord();
                newRecord.setOrganizationId(organizationRecord.getId());
                return newRecord;
            }, this::refreshAll);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Clubs");
    }

    @Override
    protected Condition initialCondition() {
        return CLUB.ORGANIZATION_ID.eq(organizationRecord.getId());
    }

    @Override
    protected List<OrderField<?>> initialSort() {
        return List.of(CLUB.ABBREVIATION.asc());
    }
}
