package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.javazen.telegram.bot.datasource.model.*;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class CriteriaChatDataSource implements ChatDataSource {
    private static final String ACTIVITY_CHART_SQL = "select generate_series, " +
            "u.user_id, u.first_name, u.last_name, u.username, " +
            "count(*) as count, sum(text_length) as length, sum(score) as score " +
            "from generate_series(cast(:from as TIMESTAMP), cast(:to as TIMESTAMP), cast(:period as INTERVAL)) " +
            "left join message_entity m on chat_id = :chat_id " +
            "and date >= generate_series and date < generate_series + cast(:period as INTERVAL) " +
            "left join user_entity u on m.user_id = u.user_id " +
            "group by generate_series, u.user_id, u.first_name, u.last_name, u.username";

    private static final String WORD_USAGE_SQL = "select word, sum(global) as global, sum(local) as local, sum(local)-sum(global) as delta " +
            "from (select word, 1000000.0 * count(word) / (select count(word) from message_entity_words) as global, 0 as local " +
            "      from message_entity_words w " +
            "      group by word " +
            "      union all " +
            "      select word, 0, 1000000.0 * count(word) / (select count(word) " +
            "                                                 from message_entity_words w " +
            "                                                        join message_entity m " +
            "                                                          on w.message_id = m.message_id and w.chat_id = m.chat_id " +
            "                                                 where m.chat_id = :chat_id " +
            "                                                   and date between cast(:from as TIMESTAMP) and cast(:to as TIMESTAMP)) " +
            "      from message_entity_words w " +
            "             join message_entity m on w.message_id = m.message_id and w.chat_id = m.chat_id " +
            "      where m.chat_id = :chat_id " +
            "        and date between cast(:from as TIMESTAMP) and cast(:to as TIMESTAMP) " +
            "      group by word) temp " +
            "where word like :search " +
            "group by word " +
            "order by :order_column :order_dir " +
            "limit :limit offset :offset";
    private static final String TOTAL_WORDS_COUNT = "select count(*) from (select distinct word from message_entity_words) as temp";
    private static final String FILTERED_WORDS_COUNT = "select count(*) from (select distinct word from message_entity_words where word like :search) as temp";

    private EntityManager entityManager;

    @Override
    public List<UserStatistic> topActiveUsers(Long chatId, DateRange dateRange) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserStatistic> query = builder.createQuery(UserStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        Join<MessageEntity, UserEntity> userJoin = messages.join(MessageEntity_.user);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        query.groupBy(userJoin);

        Expression<Long> count = builder.count(messages);
        Expression<Long> length = builder.sumAsLong(messages.get(MessageEntity_.textLength));
        Expression<Double> score = builder.sum(messages.get(MessageEntity_.score));
        query.select(builder.construct(UserStatistic.class, userJoin, count, length, score));
        query.orderBy(builder.desc(score));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<PeriodUserStatistic> activityChart(Long chatId, DateRange dateRange, TimeInterval interval, TimeZone timeZone) {
        Query nativeQuery = entityManager.createNativeQuery(ACTIVITY_CHART_SQL);
        nativeQuery.setParameter("chat_id", chatId);
        nativeQuery.setParameter("from", dateRange.getFrom());
        nativeQuery.setParameter("to", dateRange.getTo());
        nativeQuery.setParameter("period", interval.getInterval() + " " + interval.getUnit());
        List<Object[]> resultList = nativeQuery.getResultList();
        DateFormat format = new SimpleDateFormat(interval.getUnit().getDatetimeFormat());
        format.setTimeZone(timeZone);
        return resultList.stream().map(obj -> mapToPeriodUserStatistic(obj, format)).collect(Collectors.toList());
    }

    private static PeriodUserStatistic mapToPeriodUserStatistic(Object[] arr, DateFormat format) {
        String period = format.format((Timestamp) arr[0]);
        if (arr[1] == null) {
            return new PeriodUserStatistic(period);
        } else {
            UserEntity user = new UserEntity((Integer) arr[1], (String) arr[2], (String) arr[3], (String) arr[4]);
            long count = ((BigInteger) arr[5]).longValue();
            long length = ((BigInteger) arr[6]).longValue();
            double score = (Double) arr[7];
            return new PeriodUserStatistic(period, user, count, length, score);
        }
    }

    @Override
    public List<CountStatistic> topStickers(Long chatId, DateRange dateRange, Integer maxResults) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountStatistic> query = builder.createQuery(CountStatistic.class);

        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.fileType), FileType.STICKER),
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        Path<String> fileId = messages.get(MessageEntity_.fileId);
        query.groupBy(fileId);

        Expression<Long> count = builder.count(messages);
        query.select(builder.construct(CountStatistic.class, fileId, count));
        query.orderBy(builder.desc(count));

        return entityManager.createQuery(query)
                .setMaxResults(Optional.ofNullable(maxResults).orElse(5))
                .getResultList();
    }

    @Override
    public List<CountStatistic> botUsagesByModule(Long chatId, DateRange dateRange) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountStatistic> query = builder.createQuery(CountStatistic.class);

        Root<BotUsageLog> botUsages = query.from(BotUsageLog.class);
        Join<BotUsageLog, MessageEntity> messageJoin = botUsages.join(BotUsageLog_.source);

        query.where(
                builder.equal(messageJoin.get(MessageEntity_.chat), chatId),
                builder.between(messageJoin.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        query.groupBy(botUsages.get(BotUsageLog_.moduleName));

        Expression<Long> count = builder.count(botUsages);
        query.select(builder.construct(CountStatistic.class, botUsages.get(BotUsageLog_.moduleName), count));
        query.orderBy(builder.desc(count));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public DataTableResponse<WordUsageStatistic> wordsUsageStatistic(Long chatId, DateRange dateRange, DataTableRequest request) {
        List<String> columns = Arrays.asList("word", "global", "local", "delta");
        String querySql = WORD_USAGE_SQL
                .replace(":order_column", columns.get(request.getOrderColumn()))
                .replace(":order_dir", request.getOrderDir());
        Query nativeQuery = entityManager.createNativeQuery(querySql);
        String searchValue = resolveSearchValue(request);
        nativeQuery.setParameter("search", searchValue);
        nativeQuery.setParameter("chat_id", chatId);
        nativeQuery.setParameter("from", dateRange.getFrom());
        nativeQuery.setParameter("to", dateRange.getTo());
        nativeQuery.setParameter("offset", request.getStart());
        nativeQuery.setParameter("limit", request.getLength());
        List<Object[]> resultList = nativeQuery.getResultList();
        List<WordUsageStatistic> statisticList = resultList.stream()
                .map(this::mapToWordUsageStatistic)
                .collect(Collectors.toList());
        Integer recordsTotal = totalWordsCount();
        Integer recordsFiltered = request.getSearchValue() == null ? recordsTotal : filteredWordsCount(searchValue);
        return DataTableResponse.<WordUsageStatistic>builder()
                .draw(request.getDraw())
                .data(statisticList)
                .recordsTotal(recordsTotal)
                .recordsFiltered(recordsFiltered)
                .build();
    }

    private WordUsageStatistic mapToWordUsageStatistic(Object[] row) {
        return new WordUsageStatistic((String) row[0], (BigDecimal) row[1], (BigDecimal) row[2], (BigDecimal) row[3]);
    }

    private String resolveSearchValue(DataTableRequest request) {
        String searchValue = request.getSearchValue();
        if (searchValue == null) {
            return "%";
        } else if (searchValue.contains("%") || searchValue.contains("*")) {
            return searchValue;
        } else {
            return searchValue + "%";
        }
    }

    private int totalWordsCount() {
        BigInteger result = (BigInteger) entityManager.createNativeQuery(TOTAL_WORDS_COUNT).getSingleResult();
        return result.intValue();
    }

    private int filteredWordsCount(String search) {
        BigInteger result = (BigInteger) entityManager.createNativeQuery(FILTERED_WORDS_COUNT).setParameter("search", search).getSingleResult();
        return result.intValue();
    }

    @Override
    public Long messagesCount(Long chatId, DateRange dateRange) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<MessageEntity> messages = query.from(MessageEntity.class);
        query.where(
                builder.equal(messages.get(MessageEntity_.chat), chatId),
                builder.between(messages.get(MessageEntity_.date), dateRange.getFrom(), dateRange.getTo()));
        query.select(builder.count(messages));
        List<Long> resultList = entityManager.createQuery(query)
                .getResultList();
        return resultList.isEmpty() ? 0L : resultList.get(0);
    }
}
