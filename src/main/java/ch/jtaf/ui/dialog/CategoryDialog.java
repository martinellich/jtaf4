package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.CategoryEventRecord;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.domain.*;
import ch.jtaf.ui.converter.JtafStringToIntegerConverter;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import org.jooq.UpdatableRecord;

import java.io.Serial;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static ch.jtaf.db.tables.CategoryEvent.CATEGORY_EVENT;

public class CategoryDialog extends EditDialog<CategoryRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    private final long organizationId;

    private final transient CategoryEventDAO categoryEventDAO;
    private final transient EventDAO eventDAO;

    private Grid<CategoryEventVO> categoryEventsGrid;

    public CategoryDialog(String title, CategoryDAO categoryDAO, CategoryEventDAO categoryEventDAO,
                          EventDAO eventDAO, long organizationId) {
        super(title, "1600px", categoryDAO);
        this.categoryEventDAO = categoryEventDAO;
        this.eventDAO = eventDAO;
        this.organizationId = organizationId;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void createForm() {
        var abbreviation = new TextField(getTranslation("Abbreviation"));
        abbreviation.setAutoselect(true);
        abbreviation.setAutofocus(true);
        abbreviation.setRequiredIndicatorVisible(true);

        binder.forField(abbreviation)
            .withValidator(new NotEmptyValidator(this))
            .bind(CategoryRecord::getAbbreviation, CategoryRecord::setAbbreviation);

        var name = new TextField(getTranslation("Name"));
        name.setAutoselect(true);
        name.setRequiredIndicatorVisible(true);

        binder.forField(name)
            .withValidator(new NotEmptyValidator(this))
            .bind(CategoryRecord::getName, CategoryRecord::setName);

        var gender = new Select<String>();
        gender.setLabel(getTranslation("Gender"));
        gender.setRequiredIndicatorVisible(true);
        gender.setItems(Gender.valuesAsStrings());

        binder.forField(gender)
            .bind(CategoryRecord::getGender, CategoryRecord::setGender);

        var yearFrom = new TextField(getTranslation("Year.From"));
        yearFrom.setAutoselect(true);
        yearFrom.setRequiredIndicatorVisible(true);

        binder.forField(yearFrom)
            .withConverter(new JtafStringToIntegerConverter("May.not.be.empty"))
            .withNullRepresentation(-1)
            .bind(CategoryRecord::getYearFrom, CategoryRecord::setYearFrom);

        var yearTo = new TextField(getTranslation("Year.To"));
        yearTo.setAutoselect(true);
        yearTo.setRequiredIndicatorVisible(true);

        binder.forField(yearTo)
            .withConverter(new JtafStringToIntegerConverter("May.not.be.empty"))
            .withNullRepresentation(-1)
            .bind(CategoryRecord::getYearTo, CategoryRecord::setYearTo);

        formLayout.add(abbreviation, name, gender, yearFrom, yearTo);

        categoryEventsGrid = new Grid<>();
        categoryEventsGrid.setHeight("380px");
        categoryEventsGrid.setId("category-events-grid");
        categoryEventsGrid.addColumn(CategoryEventVO::abbreviation).setHeader(getTranslation("Abbreviation")).setAutoWidth(true);
        categoryEventsGrid.addColumn(CategoryEventVO::name).setHeader(getTranslation("Name")).setAutoWidth(true);
        categoryEventsGrid.addColumn(CategoryEventVO::gender).setHeader(getTranslation("Gender")).setAutoWidth(true);
        categoryEventsGrid.addColumn(CategoryEventVO::eventType).setHeader(getTranslation("Event.Type")).setAutoWidth(true);
        categoryEventsGrid.addColumn(CategoryEventVO::a).setHeader("A").setAutoWidth(true);
        categoryEventsGrid.addColumn(CategoryEventVO::b).setHeader("B").setAutoWidth(true);
        categoryEventsGrid.addColumn(CategoryEventVO::c).setHeader("C").setAutoWidth(true);
        categoryEventsGrid.addColumn(CategoryEventVO::position).setHeader(getTranslation("Position")).setAutoWidth(true);

        var addEvent = new Button(getTranslation("Add.Event"));
        addEvent.setId("add-event");
        addEvent.addClickListener(event -> {
            SearchEventDialog dialog = new SearchEventDialog(eventDAO, organizationId, binder.getBean(), this::onAssignEvent);
            dialog.open();
        });

        categoryEventsGrid.addComponentColumn(categoryRecord -> {
            Button remove = new Button(getTranslation("Remove"));
            remove.addThemeVariants(ButtonVariant.LUMO_ERROR);
            remove.addClickListener(event -> {
                removeEventFromCategory(categoryRecord);
                categoryEventsGrid.setItems(getCategoryEvents());
                categoryEventsGrid.getDataProvider().refreshAll();
            });

            var horizontalLayout = new HorizontalLayout(remove);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(addEvent).setKey("edit-column").setAutoWidth(true);

        add(categoryEventsGrid);
    }

    private void onAssignEvent(SearchEventDialog.AssignEvent assignEvent) {
        var categoryEvent = new CategoryEventRecord();
        categoryEvent.setCategoryId(binder.getBean().getId());
        categoryEvent.setEventId(assignEvent.getEventRecord().getId());

        var newPosition = getCategoryEvents().stream()
            .max(Comparator.comparingInt(CategoryEventVO::position))
            .map(categoryEventVO -> categoryEventVO.position() + 1)
            .orElse(0);
        categoryEvent.setPosition(newPosition);

        categoryEventDAO.save(categoryEvent);

        categoryEventsGrid.setItems(getCategoryEvents());
        categoryEventsGrid.getDataProvider().refreshAll();
    }

    @Override
    public void open(UpdatableRecord<?> updatableRecord, Consumer<CategoryRecord> afterSave) {
        super.open(updatableRecord, afterSave);

        categoryEventsGrid.setItems(getCategoryEvents());
    }

    private List<CategoryEventVO> getCategoryEvents() {
        CategoryRecord categoryRecord = binder.getBean();
        if (categoryRecord != null && categoryRecord.getId() != null) {
            return categoryEventDAO.findAllByCategoryId(categoryRecord.getId());
        } else {
            return Collections.emptyList();
        }
    }

    private void removeEventFromCategory(CategoryEventVO categoryEventVO) {
        new ConfirmDialog(
            "remove-event-from-category-confirm-dialog",
            getTranslation("Confirm"),
            getTranslation("Are.you.sure"),
            getTranslation("Remove"), e -> {
            categoryEventDAO.delete(
                CATEGORY_EVENT.CATEGORY_ID.eq(categoryEventVO.categoryId())
                    .and(CATEGORY_EVENT.EVENT_ID.eq(categoryEventVO.eventId())));
            categoryEventsGrid.setItems(getCategoryEvents());
            categoryEventsGrid.getDataProvider().refreshAll();
        },
            getTranslation("Cancel"), e -> {
        }).open();
    }
}
