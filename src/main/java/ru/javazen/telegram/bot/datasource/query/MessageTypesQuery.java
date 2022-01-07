package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.javazen.telegram.bot.datasource.model.BaseCount;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageTypesQuery {
    /**
     * %s = object_field
     */
    private static final String MESSAGE_TYPES_SQL = "select " +
            "case " +
            "  when forward_user_id is not null then 'FORWARD' " +
            "  when event_type is not null then 'EVENT' " +
            "  when file_type is not null then file_type " +
            "  else 'TEXT' " +
            "end as type, " +
            "count(message_id) as count " +
            "from message_entity " +
            "where %s = :object_id " +
            "  and date >= cast(:from as TIMESTAMP) and date < cast(:to as TIMESTAMP) " +
            "group by type";

    private final EntityManager entityManager;

    public List<BaseCount<String>> getChatMessagesByTypes(Long userId, DateRange dateRange) {
        return queryTemplate(userId, dateRange, "chat_id");
    }

    public List<BaseCount<String>> getUserMessagesByTypes(Long userId, DateRange dateRange) {
        return queryTemplate(userId, dateRange, "user_id");
    }

    public List<BaseCount<String>> queryTemplate(Long userId, DateRange dateRange, String objectField) {
        String sqlString = String.format(MESSAGE_TYPES_SQL, objectField);
        Query query = entityManager.createNativeQuery(sqlString)
                .setParameter("object_id", userId)
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo());

        return QueryUtils.getResultStream(query)
                .map(arr -> new BaseCount<>((String) arr[0], ((Number) arr[1]).longValue()))
                .sorted(Comparator.comparing(BaseCount::getSubject))
                .collect(Collectors.toList());
    }
}
