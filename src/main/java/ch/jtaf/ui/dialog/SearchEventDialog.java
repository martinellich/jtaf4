package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.EventRecord;
import ch.jtaf.domain.EventDAO;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
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

import static ch.jtaf.db.tables.Event.EVENT;
import static org.jooq.impl.DSL.upper;

public class SearchEventDialog extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String FULLSCREEN = "fullscreen";

    private boolean isFullScreen = false;
    private final Div content;
    private final Button toggle;

    private final ConfigurableFilterDataProvider<EventRecord, Void, String> dataProvider;

    public SearchEventDialog(EventDAO eventDAO, long organizationId, CategoryRecord categoryRecord,
                             ComponentEventListener<AssignEvent> assignEventListener) {
        setId("search-event-dialog");

        addListener(AssignEvent.class, assignEventListener);

        setDraggable(true);
        setResizable(true);

        setHeaderTitle(getTranslation("Events"));

        toggle = new Button(VaadinIcon.EXPAND_SQUARE.create());
        toggle.setId("search-event-dialog-toggle");
        toggle.addClickListener(event -> toggle());

        var close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        getHeader().add(toggle, close);

        var filter = new TextField(getTranslation("Filter"));
        filter.setId("event-filter");
        filter.setAutoselect(true);
        filter.setAutofocus(true);
        filter.setValueChangeMode(ValueChangeMode.EAGER);

        CallbackDataProvider<EventRecord, String> callbackDataProvider = DataProvider.fromFilteringCallbacks(
            query -> eventDAO.findAllByOrganizationGenderCategory(
                organizationId, categoryRecord.getGender(), categoryRecord.getId(), createCondition(query),
                query.getOffset(), query.getLimit()).stream(),
            query -> eventDAO.countByOrganizationGenderCategory(
                organizationId, categoryRecord.getGender(), categoryRecord.getId(), createCondition(query)));

        dataProvider = callbackDataProvider.withConfigurableFilter();

        var grid = new Grid<EventRecord>();
        grid.setId("events-grid");
        grid.setItems(dataProvider);
        grid.setHeight("calc(100% - 60px");

        grid.addColumn(EventRecord::getAbbreviation).setHeader(getTranslation("Abbreviation")).setSortable(true).setAutoWidth(true).setKey(EVENT.ABBREVIATION.getName());
        grid.addColumn(EventRecord::getName).setHeader(getTranslation("Name")).setSortable(true).setAutoWidth(true).setKey(EVENT.NAME.getName());
        grid.addColumn(EventRecord::getGender).setHeader(getTranslation("Gender")).setSortable(true).setAutoWidth(true).setKey(EVENT.GENDER.getName());
        grid.addColumn(EventRecord::getEventType).setHeader(getTranslation("Event.Type")).setSortable(true).setAutoWidth(true).setKey(EVENT.EVENT_TYPE.getName());
        grid.addColumn(EventRecord::getA).setHeader("A").setAutoWidth(true);
        grid.addColumn(EventRecord::getA).setHeader("B").setAutoWidth(true);
        grid.addColumn(EventRecord::getA).setHeader("C").setAutoWidth(true);

        grid.addComponentColumn(eventRecord -> new Button(getTranslation("Assign.Event"), e -> {
            fireEvent(new AssignEvent(this, eventRecord));

            Notification.show(getTranslation("Event.assigned"), 6000, Notification.Position.TOP_END);
            dataProvider.refreshAll();
        })).setAutoWidth(true).setKey("assign-column");

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
                return EVENT.ID.eq(Long.valueOf(filterString));
            } else {
                return upper(EVENT.ABBREVIATION).like(filterString.toUpperCase() + "%")
                    .or(upper(EVENT.NAME).like(filterString.toUpperCase() + "%"));
            }
        } else {
            return DSL.condition("1 = 1");
        }
    }

    public static class AssignEvent extends ComponentEvent<SearchEventDialog> {

        private final EventRecord eventRecord;

        public AssignEvent(SearchEventDialog source, EventRecord eventRecord) {
            super(source, false);
            this.eventRecord = eventRecord;
        }

        public EventRecord getEventRecord() {
            return eventRecord;
        }
    }
}
