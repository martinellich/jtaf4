package ch.jtaf.ui;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.domain.EventDAO;
import ch.jtaf.ui.dialog.EventDialog;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.OrderField;

import java.io.Serial;
import java.util.List;

import static ch.jtaf.db.tables.Event.EVENT;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@Route(layout = MainLayout.class)
public class EventsView extends ProtectedGridView<EventRecord> {

    @Serial
    private static final long serialVersionUID = 1L;
    private final EventDialog dialog;

    public EventsView(EventDAO eventDAO, OrganizationProvider organizationProvider) {
        super(eventDAO, organizationProvider, EVENT);

        setHeightFull();

        dialog = new EventDialog(getTranslation("Event"), eventDAO);

        createGrid();
        add(grid);
    }

    private void createGrid() {
        grid.setId("events-grid");

        grid.addColumn(EventRecord::getAbbreviation).setHeader(getTranslation("Abbreviation")).setSortable(true).setAutoWidth(true).setKey(EVENT.ABBREVIATION.getName());
        grid.addColumn(EventRecord::getName).setHeader(getTranslation("Name")).setSortable(true).setAutoWidth(true).setKey(EVENT.NAME.getName());
        grid.addColumn(EventRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true).setAutoWidth(true).setKey(EVENT.GENDER.getName());
        grid.addColumn(EventRecord::getEventType).setHeader(getTranslation("Event.Type")).setSortable(true).setAutoWidth(true).setKey(EVENT.EVENT_TYPE.getName());
        grid.addColumn(EventRecord::getA).setHeader("A").setAutoWidth(true);
        grid.addColumn(EventRecord::getB).setHeader("B").setAutoWidth(true);
        grid.addColumn(EventRecord::getC).setHeader("C").setAutoWidth(true);

        addActionColumnAndSetSelectionListener(JooqDAO
            , grid, dialog, eventRecord -> refreshAll(), () -> {
            var newRecord = EVENT.newRecord();
            newRecord.setOrganizationId(organizationRecord.getId());
            return newRecord;
        }, this::refreshAll);
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Events");
    }

    @Override
    protected Condition initialCondition() {
        return EVENT.ORGANIZATION_ID.eq(organizationRecord.getId());
    }

    @Override
    protected List<OrderField<?>> initialSort() {
        return List.of(EVENT.GENDER.asc(), EVENT.ABBREVIATION.asc());
    }
}
