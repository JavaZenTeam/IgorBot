package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.TimestampEntityTypesCount;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ActiveEntitiesChartQuery {
    private final EntityManager entityManager;

    private static final String SQL = "SELECT generate_series, count(chat_id) as chat_count, count(user_id) as user_count " +
            "FROM ( " +
            " SELECT generate_series, chat_id, user_id " +
            " FROM generate_series(cast(:from as TIMESTAMP), cast(:to as TIMESTAMP), cast(:period as INTERVAL)) " +
            " LEFT JOIN message_entity " +
            "  ON date >= generate_series " +
            "  AND date < generate_series + cast(:period as INTERVAL) " +
            " GROUP BY generate_series, CUBE(chat_id, user_id) " +
            " HAVING (chat_id is null) OR (user_id is null)" +
            ") temp " +
            "GROUP BY generate_series";

    public List<TimestampEntityTypesCount> getActiveEntitiesChart(DateRange dateRange, TimeInterval interval) {
        Query query = entityManager.createNativeQuery(SQL);
        query.setParameter("from", dateRange.getFrom());
        query.setParameter("to", dateRange.getTo());
        query.setParameter("period", interval.getQuantity() + " " + interval.getUnit());
        return QueryUtils.getResultList(query, QueryUtils::mapPeriodEntityTypesCount);
    }
}
