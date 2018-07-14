package ru.javazen.telegram.bot.handling.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.objects.Update;
import ru.javazen.telegram.bot.handling.annotation.Handling;
import ru.javazen.telegram.bot.handling.annotation.UpdateType;

@Aspect
@Component
public class HandlingAspect {

    @Pointcut("execution(* (@ru.javazen.telegram.bot.handling.annotation.Handling *).*(..))")
    private void handlingClass() {}

    @Pointcut("execution(* *.handle(org.telegram.telegrambots.api.objects.Update, org.telegram.telegrambots.bots.AbsSender))")
    private void handleMethod() {}

    @Around("handlingClass() && handleMethod()")
    public Object checkUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Update update = (Update) joinPoint.getArgs()[0];
        UpdateType[] updateTypes = extractHandlingTypes(joinPoint);

        if (!checkUpdate(update, updateTypes)) {
            return false;
        }
        return joinPoint.proceed();
    }

    private UpdateType[] extractHandlingTypes(ProceedingJoinPoint joinPoint) {
        Class clazz = joinPoint.getSignature().getDeclaringType();
        Handling handling = (Handling) clazz.getAnnotation(Handling.class);
        return handling.value();
    }

    private boolean checkUpdate(Update update, UpdateType[] updateTypes) {
        boolean result = false;
        for (UpdateType updateType : updateTypes) {
            switch (updateType) {
                case MESSAGE:
                    result |= update.hasMessage();
                    break;
                case CHANNEL_POST:
                    result |= update.hasChannelPost();
                    break;
                case INLINE_QUERY:
                    result |= update.hasInlineQuery();
                    break;
                case CALLBACK_QUERY:
                    result |= update.hasCallbackQuery();
                    break;
                case EDITED_MESSAGE:
                    result |= update.hasEditedMessage();
                    break;
                case SHIPPING_QUERY:
                    result |= update.hasShippingQuery();
                    break;
                case PRE_CHECKOUT_QUERY:
                    result |= update.hasPreCheckoutQuery();
                    break;
                case CHOSEN_INLINE_QUERY:
                    result |= update.hasChosenInlineQuery();
                    break;
                case EDITED_CHANNEL_POST:
                    result |= update.hasEditedMessage();
                    break;
            }
        }
        return result;
    }
}
