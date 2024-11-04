package ch.jtaf.ui;

import ch.jtaf.configuration.security.OrganizationProvider;
import ch.jtaf.ui.component.JooqDataProviderProducer;
import ch.martinelli.oss.jooqspring.JooqRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import org.jooq.Condition;
import org.jooq.OrderField;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

import java.io.Serial;
import java.util.List;

public abstract class ProtectedGridView<R extends UpdatableRecord<R>> extends ProtectedView {

    @Serial
    private static final long serialVersionUID = 1L;

    final ConfigurableFilterDataProvider<R, Void, String> dataProvider;
    final Grid<R> grid;
    protected final transient JooqRepository<?, R, ?> jooqRepository;

    protected ProtectedGridView(JooqRepository<?, R, ?> jooqRepository, OrganizationProvider organizationProvider, Table<R> table) {
        super(organizationProvider);
        this.jooqRepository = jooqRepository;

        grid = new Grid<>();
        grid.setHeightFull();

        dataProvider = new JooqDataProviderProducer<>(jooqRepository, table, this::initialCondition, this::initialSort).getDataProvider();

        grid.setItems(dataProvider);

        setHeightFull();
    }

    protected abstract Condition initialCondition();

    protected abstract List<OrderField<?>> initialSort();

    @Override
    protected void refreshAll() {
        dataProvider.refreshAll();
    }

}
