package ru.javazen.telegram.bot.datasource.query;

import lombok.experimental.UtilityClass;
import ru.javazen.telegram.bot.datasource.model.PeriodStatistic;
import ru.javazen.telegram.bot.datasource.model.Statistic;

import javax.persistence.Query;
import java.lang.reflect.Constructor;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@UtilityClass
public class QueryUtils {
    @SuppressWarnings("unchecked")
    public Stream<Object[]> getResultStream(Query query) {
        return query.getResultStream();
    }

    public <T> List<T> getResultList(Query query, Function<Object[], T> mappingFunction) {
        return getResultStream(query).map(mappingFunction).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T> Class<Statistic<T>> statisticClassFor(Class<T> target) {
        return (Class<Statistic<T>>) ((Class<?>) Statistic.class);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<PeriodStatistic<T>> periodStatisticClassFor(Class<T> target) {
        return (Class<PeriodStatistic<T>>) ((Class<?>) PeriodStatistic.class);
    }

    public <T> PeriodStatistic<T> mapFullPeriodStatistic(Object[] arr, Class<T> subjectClass) {
        Timestamp period = (Timestamp) arr[0];
        if (arr[4] == null) {
            return new PeriodStatistic<>(period);
        } else {
            long count = castLong(arr[1]);
            long length = castLong(arr[2]);
            double score = castDouble(arr[3]);

            T subject = QueryUtils.constructObject(Arrays.copyOfRange(arr, 4, arr.length), subjectClass);
            return new PeriodStatistic<>(period, subject, count, length, score);
        }
    }

    public <T> PeriodStatistic<T> mapCountPeriodStatistic(Object[] arr, Class<T> subjectClass) {
        Timestamp period = (Timestamp) arr[0];
        if (arr[2] == null) {
            return new PeriodStatistic<>(period);
        } else {
            long count = castLong(arr[1]);
            T subject = QueryUtils.constructObject(Arrays.copyOfRange(arr, 2, arr.length), subjectClass);
            return new PeriodStatistic<>(period, subject, count);
        }
    }

    public <T> Statistic<T> mapFullStatistic(Object[] arr, Class<T> subjectClass) {
        long count = castLong(arr[0]);
        long length = castLong(arr[1]);
        double score = castDouble(arr[2]);
        T subject = QueryUtils.constructObject(Arrays.copyOfRange(arr, 3, arr.length), subjectClass);
        return new Statistic<>(subject, count, length, score);
    }

    public <T> Statistic<T> mapCountStatistic(Object[] arr, Class<T> subjectClass) {
        long count = castLong(arr[0]);
        T subject = QueryUtils.constructObject(Arrays.copyOfRange(arr, 1, arr.length), subjectClass);
        return new Statistic<>(subject, count);
    }

    @SuppressWarnings("unchecked")
    public <T> T constructObject(Object[] rawResult, Class<T> objectClass) {
        Class<?>[] paramTypes = Arrays.stream(rawResult)
                .map(object -> object == null ? null : object.getClass())
                .toArray(Class[]::new);
        return Arrays.stream(objectClass.getConstructors())
                .filter(constructor -> constructor.getParameterCount() == rawResult.length)
                .filter(constructor -> IntStream.range(0, paramTypes.length).allMatch(i -> paramTypes[i] == null
                        || constructor.getParameterTypes()[i].isAssignableFrom(paramTypes[i])))
                .map(constructor -> newInstance((Constructor<T>) constructor, rawResult))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Suitable constructor is not found"));
    }

    private <T> T newInstance(Constructor<T> constructor, Object[] params) {
        try {
            return constructor.newInstance(params);
        } catch (Exception e) {
            return null;
        }
    }

    private double castDouble(Object object) {
        return ((Number) object).doubleValue();
    }

    private long castLong(Object object) {
        return ((Number) object).longValue();
    }
}
