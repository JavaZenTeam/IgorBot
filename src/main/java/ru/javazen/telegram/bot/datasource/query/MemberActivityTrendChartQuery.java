package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.javazen.telegram.bot.datasource.model.TimestampMessageStatistic;
import ru.javazen.telegram.bot.datasource.model.TimeInterval;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.MessageFormat;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class MemberActivityTrendChartQuery {
    /**
     * {0} = object_field
     * {1} = subject_table
     * {2} = subject_id_field
     * {3} = subject_fields
     */
    private static final String SQL_TEMPLATE = "select generate_series, " +
            "count(message_id) as count, sum(text_length) as length, sum(score) as score, {3} " +
            "from generate_series(cast(:from as TIMESTAMP), cast(:to as TIMESTAMP), cast(:period as INTERVAL)) " +
            "left join message_entity m on {0} = :object_id " +
            "and date >= generate_series and date < generate_series + cast(:period as INTERVAL) " +
            "left join {1} o on m.{2} = o.{2} " +
            "group by generate_series, {3}";

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<TimestampMessageStatistic<UserEntity>> getChatActivity(Long chatId, DateRange dateRange, TimeInterval interval) {
        return getActivity(chatId, dateRange, interval,
                (arr -> QueryUtils.mapTimestampMessageStatistic(arr, UserEntity.class)),
                "chat_id", "user_entity", "user_id", "first_name", "last_name", "username");
    }

    @Transactional(readOnly = true)
    public List<TimestampMessageStatistic<ChatEntity>> getUserActivity(Long userId, DateRange dateRange, TimeInterval interval) {
        return getActivity(userId, dateRange, interval,
                (arr -> QueryUtils.mapTimestampMessageStatistic(arr, ChatEntity.class)),
                "user_id", "chat_entity", "chat_id", "username", "title");

    }

    private <S, T extends TimestampMessageStatistic<S>> List<T> getActivity(
            Long userId, DateRange dateRange, TimeInterval interval,
            Function<Object[], T> mappingFunction,
            String objectField, String subjectTable, String... subjectFields
    ) {
        Query nativeQuery = entityManager.createNativeQuery(generateSqlString(objectField, subjectTable, subjectFields))
                .setParameter("object_id", userId)
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo())
                .setParameter("period", interval.getQuantity() + " " + interval.getUnit());

        var dataset = QueryUtils.getResultList(nativeQuery, mappingFunction);
        dataset.forEach(item -> item.setDataset(dataset));
        return dataset;
    }

    private String generateSqlString(String objectField,
                                     String subjectTable,
                                     String... subjectFields) {
        return MessageFormat.format(SQL_TEMPLATE,
                objectField,
                subjectTable,
                subjectFields[0],
                Stream.of(subjectFields).map(s -> "o." + s).collect(Collectors.joining(", "))
        );
    }
}
