package ch.jtaf.ui.component;

import ch.jtaf.ui.dialog.ConfirmDialog;
import ch.jtaf.ui.dialog.EditDialog;
import ch.martinelli.oss.jooqspring.JooqDAO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.jooq.UpdatableRecord;
import org.jooq.exception.DataAccessException;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GridBuilder {

    private GridBuilder() {
    }

    public static <R extends UpdatableRecord<R>> void addActionColumnAndSetSelectionListener(JooqDAO<?, R, ?> JooqDAO,
                                                                                             Grid<R> grid,
                                                                                             EditDialog<R> dialog,
                                                                                             Consumer<R> afterSave,
                                                                                             Supplier<R> onNewRecord,
                                                                                             Runnable afterDelete) {
        addActionColumnAndSetSelectionListener(JooqDAO, grid, dialog, afterSave, onNewRecord, null, null, afterDelete);
    }

    public static <R extends UpdatableRecord<R>> void addActionColumnAndSetSelectionListener(JooqDAO<?, R, ?> JooqDAO,
                                                                                             Grid<R> grid,
                                                                                             EditDialog<R> dialog,
                                                                                             Consumer<R> afterSave,
                                                                                             Supplier<R> onNewRecord,
                                                                                             String insteadOfDeleteTitle,
                                                                                             Consumer<R> insteadOfDelete,
                                                                                             Runnable afterDelete) {
        var buttonAdd = new Button(grid.getTranslation("Add"));
        buttonAdd.setId("add-button");
        buttonAdd.addClickListener(event -> dialog.open(onNewRecord.get(), afterSave));

        grid.addComponentColumn(updatableRecord -> {
            var delete = new Button(insteadOfDeleteTitle != null ? insteadOfDeleteTitle : grid.getTranslation("Delete"));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            delete.addClickListener(event -> {
                if (insteadOfDelete != null) {
                    insteadOfDelete.accept(updatableRecord);
                } else {
                    new ConfirmDialog(
                        "delete-confirm-dialog",
                        grid.getTranslation("Confirm"),
                        grid.getTranslation("Are.you.sure"),
                        grid.getTranslation("Delete"), e -> {
                        try {
                            JooqDAO.delete(updatableRecord);
                            afterDelete.run();
                        } catch (DataAccessException ex) {
                            Notification.show(ex.getMessage());
                        }
                    }, grid.getTranslation("Cancel"), e -> {
                    }).open();
                }
            });

            var horizontalLayout = new HorizontalLayout(delete);
            horizontalLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
            return horizontalLayout;
        }).setTextAlign(ColumnTextAlign.END).setHeader(buttonAdd).setAutoWidth(true).setKey("edit-column");

        grid.addItemClickListener(event -> dialog.open(event.getItem(), afterSave));
    }

}
