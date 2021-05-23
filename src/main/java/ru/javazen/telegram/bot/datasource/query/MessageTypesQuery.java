package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.Statistic;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageTypesQuery {
    private static final String MESSAGE_TYPES_SQL = "select " +
            "case " +
            "  when forward_user_id is not null then 'FORWARD' " +
            "  when event_type is not null then 'EVENT' " +
            "  when file_type is not null then file_type " +
            "  else 'TEXT' " +
            "end as type, " +
            "count(message_id) as count " +
            "from message_entity " +
            "where chat_id = :object_id " +
            "  and date >= cast(:from as TIMESTAMP) and date < cast(:to as TIMESTAMP) " +
            "group by type";

    private final EntityManager entityManager;

    public List<Statistic<String>> getChatMessagesByTypes(Long userId, DateRange dateRange) {
        Query query = entityManager.createNativeQuery(MESSAGE_TYPES_SQL)
                .setParameter("object_id", userId)
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo());

        return QueryUtils.getResultStream(query)
                .map(arr -> new Statistic<>((String) arr[0], ((BigInteger) arr[1]).longValue()))
                .sorted(Comparator.comparing(Statistic::getSubject))
                .collect(Collectors.toList());
    }
}
