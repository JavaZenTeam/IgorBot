package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.EntityTypesCount;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.MessageFormat;

@Component
@RequiredArgsConstructor
public class IncomeOutcomeEntitiesQuery {
    /**
     * {0} - having aggregating operation
     * {1} - having comparing operation
     */
    private static final String SQL_TEMPLATE = "" +
            "SELECT count(chat_id) as chat_count, count(user_id) as user_count " +
            "FROM (" +
            " SELECT chat_id, user_id" +
            " FROM message_entity" +
            " WHERE date <= :to" +
            " GROUP BY CUBE (chat_id, user_id)" +
            " HAVING (chat_id IS NULL) <> (user_id IS NULL)" +
            " AND {0}(date) {1} :from" +
            ") temp";

    private final EntityManager entityManager;

    public EntityTypesCount incomeCount(DateRange dateRange) {
        return getStatistic("min", ">", dateRange);
    }

    public EntityTypesCount outcomeCount(DateRange dateRange) {
        return getStatistic("max", "<", dateRange);
    }

    private EntityTypesCount getStatistic(String min, String x, DateRange dateRange) {
        String sql = MessageFormat.format(SQL_TEMPLATE, min, x);
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("from", dateRange.getFrom());
        query.setParameter("to", dateRange.getTo());
        return QueryUtils.mapEntityTypesCount(query.getSingleResult());
    }
}