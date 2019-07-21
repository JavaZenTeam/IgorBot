package ru.javazen.telegram.bot.datasource;

import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;

import javax.persistence.criteria.CriteriaBuilder;

public class InlineLiteral<T> extends LiteralExpression<T> {

    public static <T> InlineLiteral<T> of(CriteriaBuilder criteriaBuilder, T literal) {
        return new InlineLiteral<>((CriteriaBuilderImpl) criteriaBuilder, literal);
    }

    public InlineLiteral(CriteriaBuilderImpl criteriaBuilder, T literal) {
        super(criteriaBuilder, literal);
    }

    public InlineLiteral(CriteriaBuilderImpl criteriaBuilder, Class<T> type, T literal) {
        super(criteriaBuilder, type, literal);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        if (String.class.equals(getJavaType())) {
            String literalValue = renderingContext.getDialect().inlineLiteral((String) getLiteral());
            return ValueHandlerFactory.StringValueHandler.INSTANCE.render(literalValue);
        }
        ValueHandlerFactory.ValueHandler<T> handler = ValueHandlerFactory.determineAppropriateHandler(getJavaType());
        return handler.render(getLiteral());
    }
}
