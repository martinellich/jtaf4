package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.*;
import ch.jtaf.ui.KaribuTest;
import ch.jtaf.ui.dialog.*;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.github.mvysny.kaributesting.v10.UploadKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._assert;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class SeriesViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of(Role.ADMIN));
        UI.getCurrent().getPage().reload();

        Grid<SeriesRecord> seriesGrid = navigateToSeriesList();
        GridKt._clickItem(seriesGrid, 0);

        TextField name = _get(TextField.class);
        assertThat(name.getValue()).isEqualTo("CIS 2019");
    }

    @Test
    void add_competition() {
        // Check content of competions grid
        Grid<CompetitionRecord> competitionsGrid = _get(Grid.class, spec -> spec.withId("competitions-grid"));
        assertThat(GridKt._size(competitionsGrid)).isEqualTo(2);
        assertThat(GridKt._get(competitionsGrid, 0).getName()).isEqualTo("1. CIS Twann");

        // Add competition
        _get(Button.class, spec -> spec.withId("add-button")).click();
        _assert(CompetitionDialog.class, 1);

        _get(TextField.class, spec -> spec.withLabel("Name").withValue("")).setValue("Test");
        _get(DatePicker.class, spec -> spec.withLabel("Date")).setValue(LocalDate.now());
        _get(Button.class, spec -> spec.withId("edit-save")).click();

        // Check if competition was added
        assertThat(GridKt._size(competitionsGrid)).isEqualTo(3);
        assertThat(GridKt._get(competitionsGrid, 2).getName()).isEqualTo("Test");

        // Remove competition
        GridKt._getCellComponent(competitionsGrid, 2, "edit-column")
            .getChildren()
            .filter(Button.class::isInstance)
            .findFirst()
            .map(Button.class::cast)
            .ifPresent(Button::click);

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();
        _get(Button.class, spec -> spec.withId("delete-confirm-dialog-confirm")).click();

        // Check if competition was removed
        assertThat(GridKt._size(competitionsGrid)).isEqualTo(2);
    }

    @Test
    void add_category_and_assign_event() {
        Tabs tabs = _get(Tabs.class);
        Tab categories = _get(Tab.class, spec -> spec.withLabel("Categories"));
        tabs.setSelectedTab(categories);

        // Check content of categories grid
        Grid<CategoryRecord> categoriesGrid = _get(Grid.class, spec -> spec.withId("categories-grid"));
        assertThat(GridKt._size(categoriesGrid)).isEqualTo(12);
        assertThat(GridKt._get(categoriesGrid, 0).getAbbreviation()).isEqualTo("A");

        // Add category
        _get(Button.class, spec -> spec.withId("add-button")).click();
        _assert(CategoryDialog.class, 1);

        _get(TextField.class, spec -> spec.withLabel("Abbreviation").withValue("")).setValue("1");
        _get(TextField.class, spec -> spec.withLabel("Name").withValue("")).setValue("Test");
        _get(Select.class, spec -> spec.withLabel("Gender")).setValue("M");
        _get(TextField.class, spec -> spec.withLabel("Year from")).setValue("1999");
        _get(TextField.class, spec -> spec.withLabel("Year to")).setValue("2000");
        _get(Button.class, spec -> spec.withId("edit-save")).click();

        // Check if category was added
        assertThat(GridKt._size(categoriesGrid)).isEqualTo(13);
        assertThat(GridKt._get(categoriesGrid, 0).getAbbreviation()).isEqualTo("1");

        // Select category and assign event
        GridKt._clickItem(categoriesGrid, 0);

        _get(Button.class, spec -> spec.withId("add-event")).click();

        // Test maximize and restore
        Button toggle = _get(Button.class, spec -> spec.withId("search-event-dialog-toggle"));
        toggle.click();
        toggle.click();

        Grid<EventRecord> eventsGrid = _get(Grid.class, spec -> spec.withId("events-grid"));
        assertThat(GridKt._size(eventsGrid)).isEqualTo(9);

        // Filter with text
        _get(TextField.class, spec -> spec.withId("event-filter")).setValue("w");
        assertThat(GridKt._size(eventsGrid)).isEqualTo(1);

        // Filter with number
        _get(TextField.class, spec -> spec.withId("event-filter")).setValue("2");
        assertThat(GridKt._size(eventsGrid)).isZero();

        // Remove filter
        _get(TextField.class, spec -> spec.withId("event-filter")).setValue("");
        assertThat(GridKt._size(eventsGrid)).isEqualTo(9);

        ((Button) GridKt._getCellComponent(eventsGrid, 0, "assign-column")).click();

        _get(SearchEventDialog.class, spec -> spec.withId("search-event-dialog")).close();

        // Remove event from category
        Grid<EventRecord> categoryEventsGrid = _get(Grid.class, spec -> spec.withId("category-events-grid"));
        assertThat(GridKt._size(categoryEventsGrid)).isEqualTo(1);

        GridKt._getCellComponent(categoryEventsGrid, 0, "edit-column")
            .getChildren()
            .filter(Button.class::isInstance)
            .findFirst()
            .map(Button.class::cast)
            .ifPresent(Button::click);

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class,
                spec -> spec.withId("remove-event-from-category-confirm-dialog"));
        assertThat(confirmDialog.isOpened()).isTrue();
        _get(Button.class, spec -> spec.withId("remove-event-from-category-confirm-dialog-confirm")).click();

        // Check if event was removed
        assertThat(GridKt._size(categoryEventsGrid)).isZero();

        // Remove category
        GridKt._getCellComponent(categoriesGrid, 0, "edit-column")
            .getChildren()
            .filter(Button.class::isInstance)
            .findFirst()
            .map(Button.class::cast)
            .ifPresent(Button::click);

        confirmDialog = _get(ConfirmDialog.class, spec -> spec.withId("delete-confirm-dialog"));
        assertThat(confirmDialog.isOpened()).isTrue();
        _get(Button.class, spec -> spec.withId("delete-confirm-dialog-confirm")).click();

        // Check if category was removed
        assertThat(GridKt._size(categoriesGrid)).isEqualTo(12);
    }

    @Test
    void assign_athelete() {
        Tabs tabs = _get(Tabs.class);
        Tab athletes = _get(Tab.class, spec -> spec.withLabel("Athletes"));
        tabs.setSelectedTab(athletes);

        // Check content of athletes grid
        Grid<AthleteRecord> athletesGrid = _get(Grid.class, spec -> spec.withId("athletes-grid"));
        assertThat(GridKt._size(athletesGrid)).isEqualTo(108);
        assertThat(GridKt._get(athletesGrid, 0).getLastName()).isEqualTo("Berger");

        // Assign athlete
        Button assignAthlete = _get(Button.class, spec -> spec.withId("assign-athlete"));
        assignAthlete.click();

        _assert(SearchAthleteDialog.class, 1);

        // Test maximize and restore
        Button toggle = _get(Button.class, spec -> spec.withId("toggle"));
        toggle.click();
        toggle.click();

        _get(TextField.class, spec -> spec.withLabel("Filter").withValue("")).setValue("z");

        Grid<AthleteRecord> searchAthletesGrid = _get(Grid.class, spec -> spec.withId("search-athletes-grid"));
        assertThat(GridKt._size(searchAthletesGrid)).isEqualTo(1);

        assertThat(GridKt._get(searchAthletesGrid, 0).getLastName()).isEqualTo("Zimmermann");

        GridKt._getCellComponent(searchAthletesGrid, 0, "edit-column")
            .getChildren()
            .filter(Button.class::isInstance)
            .findFirst()
            .map(Button.class::cast)
            .ifPresent(Button::click);

        // Check if athlete was assigned
        assertThat(GridKt._size(athletesGrid)).isEqualTo(109);
        assertThat(GridKt._get(athletesGrid, 40).getLastName()).isEqualTo("Hermoso");

        // Remove athlete from category
        GridKt._getCellComponent(athletesGrid, 40, "remove-column")
            .getChildren()
            .filter(component -> component instanceof Button)
            .findFirst()
            .map(component -> (Button) component)
            .ifPresent(Button::click);

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();
        _get(Button.class, spec -> spec.withId("athlete-delete-confirm-dialog-confirm")).click();

        // Check if athlete was removed
        assertThat(GridKt._size(athletesGrid)).isEqualTo(108);
    }

    @Test
    void logo_upload() {
        try {
            URL imageUrl = getClass().getClassLoader().getResource("images/logo.png");
            if (imageUrl == null) {
                fail("Image not found");
            }
            else {
                Path path = Paths.get(imageUrl.toURI());
                byte[] logoData = Files.readAllBytes(path);

                Upload upload = _get(Upload.class, spec -> spec.withId("logo-upload"));
                UploadKt._upload(upload, "logo.png", logoData);
            }
        }
        catch (URISyntaxException | IOException e) {
            fail(e.getMessage(), e);
        }
    }

}
