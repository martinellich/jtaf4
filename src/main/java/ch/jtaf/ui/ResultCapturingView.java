package ch.jtaf.ui;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.ResultRecord;
import ch.jtaf.domain.*;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import jakarta.annotation.security.RolesAllowed;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Record4;
import org.jooq.impl.DSL;

import java.io.Serial;

import static ch.jtaf.db.tables.Athlete.ATHLETE;
import static ch.jtaf.db.tables.Category.CATEGORY;
import static ch.jtaf.db.tables.Competition.COMPETITION;
import static ch.jtaf.db.tables.Result.RESULT;
import static org.jooq.impl.DSL.upper;

@RolesAllowed({Role.USER, Role.ADMIN})
@Route(layout = MainLayout.class)
public class ResultCapturingView extends VerticalLayout implements HasDynamicTitle, HasUrlParameter<String> {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final String REMOVE_RESULTS = "Remove.results";

    private final transient ResultCalculator resultCalculator;
    private final transient ResultDAO resultDAO;
    private final transient CategoryAthleteDAO categoryAthleteDAO;
    private final transient CompetitionDAO competitionDAO;
    private final transient EventDAO eventDAO;

    private final Grid<Record4<Long, String, String, Long>> grid = new Grid<>();
    private final Div form = new Div();
    private ConfigurableFilterDataProvider<Record4<Long, String, String, Long>, Void, String> dataProvider;
    private TextField resultTextField;
    private long competitionId;

    public ResultCapturingView(ResultCalculator resultCalculator, ResultDAO resultDAO,
                               CategoryAthleteDAO categoryAthleteDAO, AthleteDAO athleteDAO,
                               CompetitionDAO competitionDAO, EventDAO eventDAO) {
        this.resultCalculator = resultCalculator;
        this.resultDAO = resultDAO;
        this.categoryAthleteDAO = categoryAthleteDAO;
        this.competitionDAO = competitionDAO;
        this.eventDAO = eventDAO;

        createDataProvider(athleteDAO);

        var filter = createFilter();
        add(filter);

        createGrid();
        add(grid);

        add(form);

        grid.asSingleSelect().addValueChangeListener(this::createForm);
    }

    private void createGrid() {
        grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.ID)).setHeader("ID").setSortable(true).setAutoWidth(true).setKey(ATHLETE.ID.getName());
        grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.LAST_NAME)).setHeader(getTranslation("Last.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.LAST_NAME.getName());
        grid.addColumn(athleteRecord -> athleteRecord.get(ATHLETE.FIRST_NAME)).setHeader(getTranslation("First.Name")).setSortable(true).setAutoWidth(true).setKey(ATHLETE.FIRST_NAME.getName());
        grid.setItems(dataProvider);
        grid.setHeight("200px");
    }

    private TextField createFilter() {
        var filter = new TextField();
        filter.setId("filter");
        filter.setAutoselect(true);
        filter.setAutofocus(true);
        filter.addValueChangeListener(event -> dataProvider.setFilter(event.getValue()));
        return filter;
    }

    private void createDataProvider(AthleteDAO athleteDAO) {
        this.dataProvider = new CallbackDataProvider<>(
            query -> {
                var athletes = athleteDAO.getAthletes(competitionId, createCondition(query), query.getOffset(), query.getLimit());
                if (athletes.size() == 1) {
                    grid.select(athletes.getFirst());
                    if (resultTextField != null) {
                        resultTextField.focus();
                    }
                }
                return athletes.stream();
            },
            (Query<Record4<Long, String, String, Long>, String> query) -> {
                int count = athleteDAO.countAthletes(competitionId, createCondition(query));
                if (count == 0) {
                    form.removeAll();
                }
                return count;
            },
            athleteRecord -> athleteRecord.get(ATHLETE.ID)
        ).withConfigurableFilter();
    }

    private void createForm(AbstractField.ComponentValueChangeEvent<Grid<Record4<Long, String, String, Long>>, Record4<Long, String, String, Long>> event) {
        form.removeAll();

        if (event.getValue() != null) {
            var formLayout = new FormLayout();
            form.add(formLayout);

            var events = eventDAO.findByCategoryIdOrderByPosition(event.getValue().get(CATEGORY.ID));

            boolean first = true;
            int position = 0;
            for (var eventRecord : events) {
                var result = new TextField(eventRecord.getName());
                result.setId("result-" + position);
                formLayout.add(result);

                if (first) {
                    this.resultTextField = result;
                    first = false;
                }

                var points = new TextField();
                points.setId("points-" + position);
                points.setReadOnly(true);
                points.setEnabled(false);
                formLayout.add(points);

                var optionalResultRecord = resultDAO.getResults(competitionId,
                    event.getValue().get(ATHLETE.ID), event.getValue().get(CATEGORY.ID),
                    eventRecord.getId());

                ResultRecord resultRecord;
                if (optionalResultRecord.isPresent()) {
                    resultRecord = optionalResultRecord.get();
                    result.setValue(resultRecord.getResult());
                    points.setValue(resultRecord.getPoints() == null ? "" : resultRecord.getPoints().toString());
                } else {
                    resultRecord = RESULT.newRecord();
                    resultRecord.setPosition(position);
                    resultRecord.setEventId(eventRecord.getId());
                    resultRecord.setAthleteId(event.getValue().get(ATHLETE.ID));
                    resultRecord.setCategoryId(event.getValue().get(CATEGORY.ID));
                    resultRecord.setCompetitionId(competitionId);
                }

                var finalResultRecord = resultRecord;
                result.addValueChangeListener(ve -> {
                    var resultValue = ve.getValue();
                    finalResultRecord.setResult(resultValue);
                    finalResultRecord.setPoints(resultCalculator.calculatePoints(eventRecord, resultValue));
                    points.setValue(finalResultRecord.getPoints() == null ? "" : finalResultRecord.getPoints().toString());

                    resultDAO.save(finalResultRecord);
                });
                position++;
            }

            var dnf = new Checkbox(getTranslation("Dnf"));
            dnf.addValueChangeListener(e -> {
                try {
                    categoryAthleteDAO.setDnf(event.getValue().get(ATHLETE.ID), event.getValue().get(CATEGORY.ID), e.getValue());
                } catch (IllegalStateException ex) {
                    Notification.show(getTranslation("Set.dnf.unsuccessful"), 6000, Notification.Position.TOP_END);
                }
            });

            categoryAthleteDAO.findById(
                    new CategoryAthleteId(event.getValue().get(CATEGORY.ID), event.getValue().get(ATHLETE.ID)))
                .ifPresent(categoryAthleteRecord -> dnf.setValue(categoryAthleteRecord.getDnf()));

            form.add(dnf);

            var removeResults = new Button(getTranslation(REMOVE_RESULTS));
            removeResults.addClassName(Margin.Top.MEDIUM);
            removeResults.addClickListener(e ->
                new ConfirmDialog("remove-results",
                    getTranslation(REMOVE_RESULTS),
                    getTranslation(REMOVE_RESULTS),
                    getTranslation("Confirm"),
                    ev -> {
                        dnf.setValue(false);

                        resultDAO.delete(
                            RESULT.ATHLETE_ID.eq(event.getValue().get(ATHLETE.ID))
                                .and(RESULT.COMPETITION_ID.eq(competitionId)));

                        createForm(event);
                    },
                    getTranslation("Cancel"),
                    ev -> {
                    }).open());
            form.add(removeResults);
        }
    }

    private Condition createCondition(Query<?, ?> query) {
        var optionalFilter = query.getFilter();
        if (optionalFilter.isPresent()) {
            String filterString = (String) optionalFilter.get();
            if (StringUtils.isNumeric(filterString)) {
                return ATHLETE.ID.eq(Long.valueOf(filterString));
            } else {
                return upper(ATHLETE.LAST_NAME).like(filterString.toUpperCase() + "%")
                    .or(upper(ATHLETE.FIRST_NAME).like(filterString.toUpperCase() + "%"));
            }
        } else {
            return DSL.condition("1 = 2");
        }
    }

    @Override
    public String getPageTitle() {
        return competitionDAO.findProjectionById(competitionId)
            .map(stringStringRecord2 -> "%s | %s - %s".formatted(
                getTranslation("Enter.Results"),
                stringStringRecord2.get(COMPETITION.series().NAME),
                stringStringRecord2.get(COMPETITION.NAME)))
            .orElseGet(() -> getTranslation("Enter.Results"));
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        competitionId = Long.parseLong(parameter);
        dataProvider.refreshAll();
    }

}
