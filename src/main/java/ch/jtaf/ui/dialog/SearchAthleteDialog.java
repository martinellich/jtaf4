package ch.jtaf.ui.dialog;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.db.tables.records.AthleteRecord;
import ch.jtaf.db.tables.records.ClubRecord;
import ch.jtaf.domain.AthleteDAO;
import ch.jtaf.domain.ClubDAO;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.value.ValueChangeMode;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.io.Serial;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.ui.component.GridBuilder.addActionColumnAndSetSelectionListener;
import static org.jooq.impl.DSL.lower;

public class SearchAthleteDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String FULLSCREEN = "fullscreen";

    private boolean isFullScreen = false;
    private final Div content;
    private final Button toggle;

    private final Map<Long, ClubRecord> clubRecordMap;
    private final ConfigurableFilterDataProvider<AthleteRecord, Void, String> dataProvider;

    public SearchAthleteDialog(AthleteDAO athleteDAO, ClubDAO clubDAO, OrganizationProvider organizationProvider,
                               Long organizationId, Long seriesId, ComponentEventListener<AthleteSelectedEvent> athleteSelectedListener) {
        setDraggable(true);
        setResizable(true);

        addListener(AthleteSelectedEvent.class, athleteSelectedListener);

        setHeaderTitle(getTranslation("Athletes"));

        toggle = new Button(VaadinIcon.EXPAND_SQUARE.create());
        toggle.setId("toggle");
        toggle.addClickListener(event -> toggle());

        var close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        getHeader().add(toggle, close);

        var dialog = new AthleteDialog(getTranslation("Athlete"), athleteDAO, clubDAO, organizationProvider);

        var filter = new TextField(getTranslation("Filter"));
        filter.setAutoselect(true);
        filter.setAutofocus(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);

        var clubs = clubDAO.findByOrganizationId(organizationId);
        clubRecordMap = clubs.stream().collect(Collectors.toMap(ClubRecord::getId, clubRecord -> clubRecord));

        CallbackDataProvider<AthleteRecord, String> callbackDataProvider = DataProvider.fromFilteringCallbacks(
            query -> athleteDAO.findByOrganizationIdAndSeriesId(organizationId, seriesId, createCondition(query), query.getOffset(), query.getLimit()).stream(),
            query -> athleteDAO.countByOrganizationIdAndSeriesId(organizationId, seriesId, createCondition(query))
        );

        dataProvider = callbackDataProvider.withConfigurableFilter();

        var grid = new Grid<AthleteRecord>();
        grid.setId("search-athletes-grid");
        grid.setItems(dataProvider);
        grid.setHeight("calc(100% - 60px");

        grid.addColumn(AthleteRecord::getLastName).setHeader(getTranslation("Last.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.LAST_NAME.getName());
        grid.addColumn(AthleteRecord::getFirstName).setHeader(getTranslation("First.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.FIRST_NAME.getName());
        grid.addColumn(AthleteRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.GENDER.getName());
        grid.addColumn(AthleteRecord::getYearOfBirth).setHeader(getTranslation("Year")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.YEAR_OF_BIRTH.getName());
        grid.addColumn(athleteRecord -> athleteRecord.getClubId() == null ? null
            : clubRecordMap.get(athleteRecord.getClubId()).getAbbreviation()).setHeader(getTranslation("Club")).setAutoWidth(true);

        addActionColumnAndSetSelectionListener(athleteDAO, grid, dialog, athleteRecord -> dataProvider.refreshAll(), () -> {
            var newRecord = ATHLETE.newRecord();
            newRecord.setOrganizationId(organizationId);
            return newRecord;
        }, getTranslation("Assign.Athlete"), athleteRecord -> {
            fireEvent(new AthleteSelectedEvent(this, athleteRecord));
            close();
        }, dataProvider::refreshAll);

        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));

        content = new Div(filter, grid);
        content.setSizeFull();
        add(content);

        toggle();

        filter.focus();
    }

    private void initialSize() {
        toggle.setIcon(VaadinIcon.EXPAND_SQUARE.create());
        getElement().getThemeList().remove(FULLSCREEN);
        setHeight("auto");
        setWidth("600px");
    }

    private void toggle() {
        if (isFullScreen) {
            initialSize();
        } else {
            toggle.setIcon(VaadinIcon.COMPRESS_SQUARE.create());
            getElement().getThemeList().add(FULLSCREEN);
            setSizeFull();
            content.setVisible(true);
        }
        isFullScreen = !isFullScreen;
    }

    private Condition createCondition(Query<?, ?> query) {
        var optionalFilter = query.getFilter();
        if (optionalFilter.isPresent()) {
            String filterString = (String) optionalFilter.get();
            if (StringUtils.isNumeric(filterString)) {
                return ATHLETE.ID.eq(Long.valueOf(filterString));
            } else {
                return lower(ATHLETE.LAST_NAME).like(filterString.toLowerCase() + "%")
                    .or(lower(ATHLETE.FIRST_NAME).like(filterString.toLowerCase() + "%"));
            }
        } else {
            return DSL.condition("1 = 2");
        }
    }

    public static class AthleteSelectedEvent extends ComponentEvent<SearchAthleteDialog> {

        private final AthleteRecord athleteRecord;

        public AthleteSelectedEvent(SearchAthleteDialog source, AthleteRecord athleteRecord) {
            super(source, false);

            this.athleteRecord = athleteRecord;
        }

        public AthleteRecord getAthleteRecord() {
            return athleteRecord;
        }
    }
}
