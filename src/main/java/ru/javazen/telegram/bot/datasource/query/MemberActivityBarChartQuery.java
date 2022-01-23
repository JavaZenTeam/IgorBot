package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.javazen.telegram.bot.datasource.model.PeriodIdMessageStatistic;
import ru.javazen.telegram.bot.datasource.model.TimeGroup;
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
public class MemberActivityBarChartQuery {
    /**
     * {0} = object_field
     * {1} = subject_table
     * {2} = subject_id_field
     * {3} = subject_fields
     * {4} = period_field
     */
    private static final String SQL_TEMPLATE = "select generate_series, " +
            "count(message_id) as count, sum(text_length) as length, sum(score) as score, {3} " +
            "from generate_series(:period_start, :period_stop, :period_step) " +
            "left join message_entity m on {0} = :object_id " +
            "and date >= cast(:from as TIMESTAMP) and date < cast(:to as TIMESTAMP) " +
            "and extract({4} from timezone(:timezone, date)) >= generate_series " +
            "and extract({4} from timezone(:timezone, date)) < generate_series + :period_step " +
            "left join {1} o on m.{2} = o.{2} " +
            "group by generate_series, {3} " +
            "order by generate_series";

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<PeriodIdMessageStatistic<UserEntity>> chatPeriodsChart(Long chatId, DateRange dateRange, TimeGroup periodType) {
        return periodsChart(chatId, dateRange, periodType,
                (arr -> QueryUtils.mapNamedPeriodMessageStatistic(arr, UserEntity.class)),
                "chat_id", "user_entity", "user_id", "first_name", "last_name", "username");
    }

    @Transactional(readOnly = true)
    public List<PeriodIdMessageStatistic<ChatEntity>> userPeriodsChart(Long userId, DateRange dateRange, TimeGroup periodType) {
        return periodsChart(userId, dateRange, periodType,
                (arr -> QueryUtils.mapNamedPeriodMessageStatistic(arr, ChatEntity.class)),
                "user_id", "chat_entity", "chat_id", "username", "title");
    }



    private <S, T extends PeriodIdMessageStatistic<S>> List<T> periodsChart(
            Long userId, DateRange dateRange, TimeGroup timeGroup,
            Function<Object[], T> mappingFunction,
            String objectField, String subjectTable, String... subjectFields
    ) {
        String sqlString = generateSqlString(timeGroup, objectField, subjectTable, subjectFields);
        Query nativeQuery = entityManager.createNativeQuery(sqlString)
                .setParameter("object_id", userId)
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo())
                .setParameter("timezone", dateRange.getTimeZone().toZoneId().toString())
                .setParameter("period_start", timeGroup.getField().getStart())
                .setParameter("period_stop", timeGroup.getField().getStop())
                .setParameter("period_step", timeGroup.getQuantity());

        var dataset = QueryUtils.getResultList(nativeQuery, mappingFunction);
        dataset.forEach(item -> item.setDataset(dataset));
        return dataset;
    }

    private String generateSqlString(TimeGroup periodType,
                                     String objectField,
                                     String subjectTable,
                                     String... subjectFields) {
        return MessageFormat.format(SQL_TEMPLATE,
                objectField,
                subjectTable,
                subjectFields[0],
                Stream.of(subjectFields).map(s -> "o." + s).collect(Collectors.joining(", ")),
                periodType.getField().name()
        );
    }
}
