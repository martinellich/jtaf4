package ch.jtaf.ui.dialog;

import ch.jtaf.db.tables.records.OrganizationRecord;
import ch.jtaf.domain.OrganizationDAO;
import ch.jtaf.ui.validator.NotEmptyValidator;
import com.vaadin.flow.component.textfield.TextField;

import java.io.Serial;

public class OrganizationDialog extends EditDialog<OrganizationRecord> {

    @Serial
    private static final long serialVersionUID = 1L;

    public OrganizationDialog(String title, OrganizationDAO organizationDAO) {
        super(title, "600px", organizationDAO);
    }

    @Override
    public void createForm() {
        var key = new TextField(getTranslation("Key"));
        key.setId("key");
        key.setAutoselect(true);
        key.setAutofocus(true);
        key.setRequiredIndicatorVisible(true);
        formLayout.add(key);

        binder.forField(key)
            .withValidator(new NotEmptyValidator(this))
            .bind(OrganizationRecord::getOrganizationKey, OrganizationRecord::setOrganizationKey);

        var name = new TextField(getTranslation("Name"));
        name.setId("name");
        name.setAutoselect(true);
        name.setRequiredIndicatorVisible(true);
        formLayout.add(name);

        binder.forField(name)
            .withValidator(new NotEmptyValidator(this))
            .bind(OrganizationRecord::getName, OrganizationRecord::setName);
    }

}
