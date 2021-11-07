package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
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
public class MemberActivityChartQuery {
    /**
     * {0} = object_field (chat_id or user_id)
     * {1} = subject_table (chat_entity or user_entity)
     * {2} = subject_id_field (user_id or chat_id)
     * {3} = subject_fields (all subject_table fields)
     * {4} = source_table (message_entity or daily_user_chat_statistic)
     * {5} = count_formula (count(message_id) or sum(count))
     */
    private static final String SQL_TEMPLATE = "select generate_series, " +
            "{5} as count, sum(text_length) as length, sum(score) as score, {3} " +
            "from generate_series(cast(:from as TIMESTAMP), cast(:to as TIMESTAMP), cast(:period as INTERVAL)) " +
            "left join {4} m on {0} = :object_id " +
            "and date >= generate_series and date < generate_series + cast(:period as INTERVAL) " +
            "left join {1} o on m.{2} = o.{2} " +
            "group by generate_series, {3}";

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<PeriodStatistic<UserEntity>> getChatActivity(Long chatId, DateRange dateRange, TimeInterval interval) {
        return getActivity(chatId, dateRange, interval,
                (arr -> QueryUtils.mapFullPeriodStatistic(arr, UserEntity.class)),
                "chat_id", "user_entity", "user_id", "first_name", "last_name", "username");
    }

    @Transactional(readOnly = true)
    public List<PeriodStatistic<ChatEntity>> getUserActivity(Long userId, DateRange dateRange, TimeInterval interval) {
        return getActivity(userId, dateRange, interval,
                (arr -> QueryUtils.mapFullPeriodStatistic(arr, ChatEntity.class)),
                "user_id", "chat_entity", "chat_id", "username", "title");

    }

    private <S, T extends PeriodStatistic<S>> List<T> getActivity(
            Long userId, DateRange dateRange, TimeInterval interval,
            Function<Object[], T> mappingFunction,
            String objectField, String subjectTable, String... subjectFields
    ) {
        String sqlString = generateSqlString(objectField, subjectTable, interval.getUnit(), subjectFields);
        Query nativeQuery = entityManager.createNativeQuery(sqlString)
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
                                     TimeInterval.Unit unit,
                                     String... subjectFields) {
        return MessageFormat.format(SQL_TEMPLATE,
                objectField,
                subjectTable,
                subjectFields[0],
                Stream.of(subjectFields).map(s -> "o." + s).collect(Collectors.joining(", ")),
                unit == TimeInterval.Unit.HOUR ? "message_entity" : "daily_user_chat_statistic",
                unit == TimeInterval.Unit.HOUR ? "count(message_id)" : "sum(count)"
        );
    }
}
