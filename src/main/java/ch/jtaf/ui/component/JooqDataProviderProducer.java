package ch.jtaf.ui.component;

import ch.martinelli.oss.jooqspring.JooqDAO;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.SortDirection;
import org.jooq.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.noCondition;
import static org.jooq.impl.DSL.upper;

public class JooqDataProviderProducer<R extends UpdatableRecord<R>> {

    private final JooqDAO<?, R, ?> jooqDAO;

    private final Table<R> table;

    private final ConfigurableFilterDataProvider<R, Void, String> dataProvider;

    private final Supplier<Condition> initialCondition;

    private final Supplier<List<OrderField<?>>> initialSort;

    public JooqDataProviderProducer(JooqDAO<?, R, ?> jooqDAO, Table<R> table, Supplier<Condition> initialCondition,
            Supplier<List<OrderField<?>>> initialSort) {
        this.jooqDAO = jooqDAO;
        this.table = table;
        this.initialCondition = initialCondition;
        this.initialSort = initialSort;

        this.dataProvider = DataProvider.fromFilteringCallbacks(this::fetch, this::count).withConfigurableFilter();
    }

    public ConfigurableFilterDataProvider<R, Void, String> getDataProvider() {
        return dataProvider;
    }

    private Stream<R> fetch(Query<R, String> query) {
        List<R> all = jooqDAO.findAll(createCondition(query), query.getOffset(), query.getLimit(),
                createOrderBy(query));
        return all.stream();
    }

    private int count(Query<R, String> query) {
        return jooqDAO.count(createCondition(query));
    }

    private Condition createCondition(Query<R, String> query) {
        var condition = noCondition();
        var filter = query.getFilter();
        if (filter.isPresent()) {
            for (var field : table.fields()) {
                if (field.getType() == String.class) {
                    // noinspection unchecked
                    condition = condition.or(upper((Field<String>) field).like(upper("%" + filter.get() + "%")));
                }
                else {
                    condition = condition.or(field.like("%" + filter.get() + "%"));
                }
            }
        }
        condition = condition.and(initialCondition.get());
        return condition;
    }

    private List<OrderField<?>> createOrderBy(Query<R, String> query) {
        if (query.getSortOrders().isEmpty()) {
            return initialSort.get();
        }
        else {
            var sortFields = new ArrayList<OrderField<?>>();
            for (var sortOrder : query.getSortOrders()) {
                var column = sortOrder.getSorted();
                var sortDirection = sortOrder.getDirection();
                var field = table.field(column);
                if (field != null) {
                    if (sortDirection == SortDirection.DESCENDING) {
                        sortFields.add(field.desc());
                    }
                    else {
                        sortFields.add(field.asc());
                    }
                }
            }
            return sortFields;
        }
    }

}
