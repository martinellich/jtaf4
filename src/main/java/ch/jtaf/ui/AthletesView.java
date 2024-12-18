package ch.jtaf.ui;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.domain.ClubDAO;
import ch.jtaf.ui.dialog.AthleteDialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.jooq.Condition;
import org.jooq.OrderField;

import java.io.Serial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;

@Route(layout = MainLayout.class)
public class AthletesView extends ProtectedGridView<AthleteRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient ClubDAO clubDAO;
    private final AthleteDialog dialog;
    private Map<Long, ClubRecord> clubRecordMap = new HashMap<>();

    public AthletesView(AthleteDAO athleteDAO, ClubDAO clubDAO, OrganizationProvider organizationProvider) {
        super(athleteDAO, organizationProvider, ATHLETE);
        this.clubDAO = clubDAO;

        setHeightFull();

        dialog = new AthleteDialog(getTranslation("Athlete"), athleteDAO, clubDAO, organizationProvider);

        var filter = createFilter();
        add(filter);

        createGrid();
        add(grid);

        filter.focus();
    }

    private void createGrid() {
        grid.setId("athletes-grid");

        grid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.LAST_NAME.getName());
        grid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.FIRST_NAME.getName());
        grid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.GENDER.getName());
        grid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.YEAR_OF_BIRTH.getName());
        grid.addColumn(athleteRecord -> athleteRecord.getClubId() == null
            ? null
            : clubRecordMap.get(athleteRecord.getClubId()).getAbbreviation()).setHeader(getTranslation("Club")).setAutoWidth(true);

        addActionColumnAndSetSelectionListener(JooqDAO, grid, dialog, athleteRecord -> refreshAll(), () -> {
            AthleteRecord newRecord = ATHLETE.newRecord();
            newRecord.setOrganizationId(organizationRecord.getId());
            return newRecord;
        }, this::refreshAll);
    }

    private TextField createFilter() {
        var filter = new TextField(getTranslation("Filter"));
        filter.setId("filter");
        filter.setAutoselect(true);
        filter.setAutofocus(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);

        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        return filter;
    }

    @Override
    protected void refreshAll() {
        super.refreshAll();
        var clubs = clubDAO.findByOrganizationId(organizationRecord.getId());
        clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));
    }

    @Override
    public String getPageTitle() {
        return getTranslation("Athletes");
    }

    @Override
    protected Condition initialCondition() {
        return ATHLETE.ORGANIZATION_ID.eq(organizationRecord.getId());
    }

    @Override
    protected List<OrderField<?>> initialSort() {
        return List.of(ATHLETE.GENDER.asc(), ATHLETE.YEAR_OF_BIRTH.asc(), ATHLETE.LAST_NAME.asc(), ATHLETE.FIRST_NAME.asc());
    }
}
