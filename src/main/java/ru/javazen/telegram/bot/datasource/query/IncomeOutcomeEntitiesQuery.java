package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.EntityTypesCount;
import ru.javazen.telegram.bot.util.DateRange;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class IncomeOutcomeEntitiesQuery {
    private static final String SQL_INCOME = "" +
            "SELECT count(chat_id) as chat_count, count(user_id) as user_count " +
            "FROM (" +
            " SELECT chat_id, user_id" +
            " FROM message_entity" +
            " WHERE date <= :to" +
            " GROUP BY CUBE (chat_id, user_id)" +
            " HAVING (chat_id IS NULL) <> (user_id IS NULL)" +
            " AND min(date) >= :from" +
            ") temp";

    private static final String SQL_OUTCOME = "" +
            "SELECT count(chat_id) as chat_count, count(user_id) as user_count " +
            "FROM (" +
            " SELECT chat_id, user_id" +
            " FROM message_entity" +
            " WHERE date < :to AND date >= :prev " +
            " GROUP BY CUBE (chat_id, user_id)" +
            " HAVING (chat_id IS NULL) <> (user_id IS NULL)" +
            " AND max(date) < :from" +
            ") temp";

    private final EntityManager entityManager;

    public EntityTypesCount incomeCount(DateRange dateRange) {
        Query query = entityManager.createNativeQuery(SQL_INCOME);
        query.setParameter("from", dateRange.getFrom());
        query.setParameter("to", dateRange.getTo());
        return QueryUtils.mapEntityTypesCount(query.getSingleResult());
    }

    public EntityTypesCount outcomeCount(DateRange dateRange) {
        long rangeMilliseconds = dateRange.getTo().getTime() - dateRange.getFrom().getTime();
        long previousMilliseconds = dateRange.getFrom().getTime() - rangeMilliseconds;
        Date previousDate = new Date(previousMilliseconds);

        Query query = entityManager.createNativeQuery(SQL_OUTCOME);
        query.setParameter("from", dateRange.getFrom());
        query.setParameter("to", dateRange.getTo());
        query.setParameter("prev", previousDate);
        return QueryUtils.mapEntityTypesCount(query.getSingleResult());
    }
}