package ch.jtaf.ui.dialog;

import ch.martinelli.oss.jooqspring.JooqDAO;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.Binder;
import org.jooq.UpdatableRecord;

import java.io.Serial;
import java.util.function.Consumer;

public abstract class EditDialog<R extends UpdatableRecord<R>> extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String FULLSCREEN = "fullscreen";

    private final String initialWidth;

    private boolean isFullScreen = false;
    private final Div content;
    private final Button toggle;

    final Binder<R> binder;
    final FormLayout formLayout;

    private transient Consumer<R> afterSave;
    private boolean initialized;

    protected EditDialog(String title, String initialWidth, JooqDAO<?, R, ?> jooqDAO) {
        this.initialWidth = initialWidth;

        setWidth(initialWidth);

        setDraggable(true);
        setResizable(true);

        setHeaderTitle(title);

        toggle = new Button(VaadinIcon.EXPAND_SQUARE.create());
        toggle.addClickListener(event -> toggleFullscreen());
        toggle.setId("toggle");

        var close = new Button(VaadinIcon.CLOSE_SMALL.create());
        close.addClickListener(event -> close());

        getHeader().add(toggle, close);

        formLayout = new FormLayout();

        binder = new Binder<>();

        content = new Div(formLayout);

        add(content);

        var save = new Button(getTranslation("Save"));
        save.setId("edit-save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            R recordToSave = binder.getBean();
            jooqDAO.save(recordToSave);

            if (afterSave != null) {
                afterSave.accept(recordToSave);
            }
            close();
        });

        var cancel = new Button(getTranslation("Cancel"));
        cancel.addClickListener(event -> close());

        getFooter().add(save, cancel);
    }

    public abstract void createForm();

    @SuppressWarnings("unchecked")
    public void open(UpdatableRecord<?> updatableRecord, Consumer<R> afterSave) {
        binder.setBean((R) updatableRecord);
        this.afterSave = afterSave;

        if (!initialized) {
            createForm();
            initialized = true;
        }

        super.open();
    }

    private void initialSize() {
        toggle.setIcon(VaadinIcon.EXPAND_SQUARE.create());
        getElement().getThemeList().remove(FULLSCREEN);
        setHeight("auto");
        setWidth(initialWidth);
    }

    private void toggleFullscreen() {
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

}
