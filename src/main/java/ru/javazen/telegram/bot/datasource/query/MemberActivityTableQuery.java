package ru.javazen.telegram.bot.datasource.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.javazen.telegram.bot.datasource.model.Statistic;
import ru.javazen.telegram.bot.model.ChatEntity;
import ru.javazen.telegram.bot.model.DailyUserChatPK_;
import ru.javazen.telegram.bot.model.DailyUserChatStatistic;
import ru.javazen.telegram.bot.model.DailyUserChatStatistic_;
import ru.javazen.telegram.bot.model.UserEntity;
import ru.javazen.telegram.bot.util.DateRange;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberActivityTableQuery {
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Statistic<UserEntity>> getChatActivity(Long chatId, DateRange dateRange) {
        return getActivity(DailyUserChatStatistic_.user, DailyUserChatStatistic_.chat, chatId, dateRange);
    }

    @Transactional(readOnly = true)
    public List<Statistic<ChatEntity>> getUserActivity(Long userId, DateRange dateRange) {
        return getActivity(DailyUserChatStatistic_.chat, DailyUserChatStatistic_.user, userId, dateRange);
    }

    private <T> List<Statistic<T>> getActivity(SingularAttribute<DailyUserChatStatistic, T> subjectAttr,
                                               SingularAttribute<DailyUserChatStatistic, ?> objectAttr,
                                               Long objectId,
                                               DateRange dateRange) {
        Class<Statistic<T>> statisticClass = QueryUtils.statisticClassFor(subjectAttr.getBindableJavaType());

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Statistic<T>> query = builder.createQuery(statisticClass);

        Root<DailyUserChatStatistic> stats = query.from(DailyUserChatStatistic.class);
        Path<LocalDate> datePath = stats.get(DailyUserChatStatistic_.pk).get(DailyUserChatPK_.date);
        Predicate datePredicate = builder.between(datePath, dateRange.getFromDate(), dateRange.getToDate());
        query.where(builder.equal(stats.get(objectAttr), objectId), datePredicate);

        Join<DailyUserChatStatistic, ?> subjectJoin = stats.join(subjectAttr);
        query.groupBy(subjectJoin);

        Expression<Long> count = builder.sum(stats.get(DailyUserChatStatistic_.count));
        Expression<Long> length = builder.sum(stats.get(DailyUserChatStatistic_.textLength));
        Expression<Double> score = builder.sum(stats.get(DailyUserChatStatistic_.score));
        query.select(builder.construct(statisticClass, subjectJoin, count, length, score));
        query.orderBy(builder.desc(score));

        List<Statistic<T>> dataset = entityManager.createQuery(query).getResultList();
        dataset.forEach(item -> item.setDataset(dataset));
        return dataset;
    }
}
