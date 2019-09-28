package ru.javazen.telegram.bot.datasource;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.javazen.telegram.bot.datasource.model.*;
import ru.javazen.telegram.bot.model.*;
import ru.javazen.telegram.bot.repository.DictionaryRepository;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
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

    private static final String WORD_USAGE_SQL = "select w.word, " +
            "count(*) as count, " +
            "(count(*) / :chat_count) - (d.count - count(*)) / (:global_count - :chat_count) as delta " +
            "from message_entity_words w " +
            "join message_entity m on w.message_id = m.message_id and w.chat_id = m.chat_id " +
            "right join dictionary_word d on w.word = d.word " +
            "where m.chat_id = :chat_id " +
            "  and date between cast(:from as TIMESTAMP) and cast(:to as TIMESTAMP) " +
            "  and w.word like :search " +
            "group by w.word, d.count " +
            "order by :order_column :order_dir " +
            "limit :limit offset :offset";
    private static final String TOTAL_WORDS_COUNT = "select count(*) " +
            "from message_entity_words w join message_entity m on w.message_id = m.message_id and w.chat_id = m.chat_id " +
            "where m.chat_id = :chat_id and date between cast(:from as TIMESTAMP) and cast(:to as TIMESTAMP)";
    private static final String FILTERED_WORDS_COUNT = "select count(*) " +
            "from message_entity_words w join message_entity m on w.message_id = m.message_id and w.chat_id = m.chat_id " +
            "where word like :search and m.chat_id = :chat_id and date between cast(:from as TIMESTAMP) and cast(:to as TIMESTAMP)";

    private EntityManager entityManager;
    private DictionaryRepository dictionaryRepository;

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public DataTableResponse<WordUsageStatistic> wordsUsageStatistic(Long chatId, DateRange dateRange, DataTableRequest request) {
        Long globalCount = dictionaryRepository.sumCounts();
        Long chatCount = chatWordsCount(chatId, dateRange);

        List<String> columns = Arrays.asList("word", "count", "delta");
        String querySql = WORD_USAGE_SQL
                .replace(":order_column", columns.get(request.getOrderColumn()))
                .replace(":order_dir", request.getOrderDir());

        Query nativeQuery = entityManager.createNativeQuery(querySql);
        String searchValue = resolveSearchValue(request);
        nativeQuery.setParameter("search", searchValue);
        nativeQuery.setParameter("chat_count", (double) chatCount);
        nativeQuery.setParameter("global_count", (double) globalCount);
        nativeQuery.setParameter("chat_id", chatId);
        nativeQuery.setParameter("from", dateRange.getFrom());
        nativeQuery.setParameter("to", dateRange.getTo());
        nativeQuery.setParameter("offset", request.getStart());
        nativeQuery.setParameter("limit", request.getLength());
        List<Object[]> resultList = nativeQuery.getResultList();
        List<WordUsageStatistic> statisticList = resultList.stream()
                .map(this::mapToWordUsageStatistic)
                .collect(Collectors.toList());

        Long recordsFiltered = StringUtils.isEmpty(request.getSearchValue())
                ? chatCount
                : filteredWordsCount(chatId, dateRange, searchValue);

        return DataTableResponse.<WordUsageStatistic>builder()
                .draw(request.getDraw())
                .data(statisticList)
                .recordsTotal(chatCount)
                .recordsFiltered(recordsFiltered)
                .build();
    }

    private WordUsageStatistic mapToWordUsageStatistic(Object[] row) {
        return new WordUsageStatistic((String) row[0], (BigInteger) row[1], (Double) row[2]);
    }

    private String resolveSearchValue(DataTableRequest request) {
        String searchValue = request.getSearchValue();
        if (searchValue == null) {
            return "%";
        } else if (searchValue.contains("%") || searchValue.contains("_")) {
            return searchValue;
        } else {
            return searchValue + "%";
        }
    }

    private long chatWordsCount(Long chatId, DateRange dateRange) {
        BigInteger result = (BigInteger) entityManager.createNativeQuery(TOTAL_WORDS_COUNT)
                .setParameter("chat_id", chatId)
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo())
                .getSingleResult();
        return result.longValue();
    }

    private long filteredWordsCount(Long chatId, DateRange dateRange, String search) {
        BigInteger result = (BigInteger) entityManager.createNativeQuery(FILTERED_WORDS_COUNT)
                .setParameter("chat_id", chatId)
                .setParameter("from", dateRange.getFrom())
                .setParameter("to", dateRange.getTo())
                .setParameter("search", search)
                .getSingleResult();
        return result.longValue();
    }

    @Override
    @Transactional(readOnly = true)
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
