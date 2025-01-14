package ch.jtaf.ui.view;

import ch.jtaf.configuration.security.Role;
import ch.jtaf.db.tables.records.CategoryRecord;
import ch.jtaf.db.tables.records.SeriesRecord;
import ch.jtaf.ui.KaribuTest;
import ch.jtaf.ui.SeriesListView;
import ch.jtaf.ui.dialog.ConfirmDialog;
import com.github.mvysny.kaributesting.v10.GridKt;
import com.github.mvysny.kaributesting.v10.NotificationsKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.assertj.core.api.Assertions.assertThat;

class SeriesListViewTest extends KaribuTest {

    @BeforeEach
    public void login() {
        login("simon@martinelli.ch", "", List.of(Role.ADMIN));
        UI.getCurrent().getPage().reload();

        navigateToSeriesList();
    }

    @Test
    void add_series() {
        // Add new series
        Button addSeries = _get(Button.class, spec -> spec.withId("add-series"));
        addSeries.click();

        TextField textField = _get(TextField.class);
        assertThat(textField.getValue()).isEmpty();

        textField.setValue("Test");

        Button save = _get(Button.class, spec -> spec.withId("save-series"));
        save.click();

        NotificationsKt.expectNotifications("Series saved");

        UI.getCurrent().navigate(SeriesListView.class);

        // Check if series was added
        Grid<SeriesRecord> seriesGrid = _get(Grid.class, spec -> spec.withId("series-grid"));
        assertThat(GridKt._size(seriesGrid)).isEqualTo(3);
        assertThat(GridKt._get(seriesGrid, 0).getName()).isEqualTo("Test");

        // Remove series
        GridKt._getCellComponent(seriesGrid, 0, "delete-column")
            .getChildren()
            .filter(Button.class::isInstance)
            .findFirst()
            .map(Button.class::cast)
            .ifPresent(Button::click);

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();
        _get(Button.class, spec -> spec.withId("delete-series-confirm-dialog-confirm")).click();

        // Check if series was removed
        assertThat(GridKt._size(seriesGrid)).isEqualTo(2);
    }

    @Test
    void add_series_and_copy_categories() {
        // Add new series
        Button addSeries = _get(Button.class, spec -> spec.withId("add-series"));
        addSeries.click();

        TextField textField = _get(TextField.class);
        assertThat(textField.getValue()).isEmpty();

        textField.setValue("Test");

        Button save = _get(Button.class, spec -> spec.withId("save-series"));
        save.click();

        NotificationsKt.expectNotifications("Series saved");

        UI.getCurrent().navigate(SeriesListView.class);

        // Check if series was added
        Grid<SeriesRecord> seriesGrid = _get(Grid.class, spec -> spec.withId("series-grid"));
        assertThat(GridKt._size(seriesGrid)).isEqualTo(3);
        assertThat(GridKt._get(seriesGrid, 0).getName()).isEqualTo("Test");

        // Navigate to SeriesView
        GridKt._clickItem(seriesGrid, 0);

        // Copy series
        _get(Button.class, spec -> spec.withId("copy-categories")).click();

        ComboBox<SeriesRecord> seriesSelection = _get(ComboBox.class, spec -> spec.withId("series-selection"));
        seriesSelection.setValue(seriesSelection.getLazyDataView().getItem(0));
        _click(_get(Button.class, spec -> spec.withId("copy-categories-copy")));

        NotificationsKt.expectNotifications("Categories copied");

        // Select Categories tab
        Tabs tabs = _get(Tabs.class);
        Tab categories = _get(Tab.class, spec -> spec.withLabel("Categories"));
        tabs.setSelectedTab(categories);

        // Check if categories have been copied
        Grid<CategoryRecord> categoriesGrid = _get(Grid.class, spec -> spec.withId("categories-grid"));
        assertThat(GridKt._size(categoriesGrid)).isEqualTo(12);
        assertThat(GridKt._get(categoriesGrid, 0).getAbbreviation()).isEqualTo("A");

        // Remove series
        UI.getCurrent().navigate(SeriesListView.class);

        GridKt._getCellComponent(seriesGrid, 0, "delete-column")
            .getChildren()
            .filter(Button.class::isInstance)
            .findFirst()
            .map(Button.class::cast)
            .ifPresent(Button::click);

        ConfirmDialog confirmDialog = _get(ConfirmDialog.class);
        assertThat(confirmDialog.isOpened()).isTrue();
        _get(Button.class, spec -> spec.withId("delete-series-confirm-dialog-confirm")).click();

        // Check if series was removed
        assertThat(GridKt._size(seriesGrid)).isEqualTo(2);
    }

}
