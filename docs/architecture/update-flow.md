# Update Flow Map

## Purpose

This document explains how an incoming Telegram update is processed and persisted.

## Sequence

```mermaid
flowchart TD
  telegramApi[TelegramAPI] --> compositeBot[CompositeBot_onUpdateReceived]
  compositeBot --> handlerChain[OrderedUpdateHandlers]
  handlerChain --> firstMatch[FirstHandlerReturnsTrue]
  firstMatch --> botAction[SendOrEditTelegramMessage]
  compositeBot --> collector[MessageCollectorService]
  collector --> repositories[JPARepositories]
```

## Behavioral Notes

- Handler order is defined in `src/main/resources/spring/botConfig.xml`.
- Update processing uses chain-of-responsibility: first matching handler wins.
- Logging and usage collection are executed in `CompositeBot` wrappers.
- Message persistence behavior depends on chat configuration and update type.

## Where To Edit

- Add/modify handler logic: `src/main/java/ru/javazen/telegram/bot/handler/`
- Add/modify filters: `src/main/java/ru/javazen/telegram/bot/filter/`
- Rewire handler order: `src/main/resources/spring/botConfig.xml`
- Adjust persistence: `src/main/java/ru/javazen/telegram/bot/service/impl/MessageCollectorServiceImpl.java`

## Adding a Handler

1. Implement the handler class under `src/main/java/ru/javazen/telegram/bot/handler/` against the right base interface from `handler/base/`.
2. Register the bean and add it to the `updateHandlers` list in `src/main/resources/spring/botConfig.xml`; mind the position — the first matching handler wins.
3. Wire filters deliberately; a broad filter early in the chain shadows handlers registered later.
4. Validate with realistic updates: the new handler triggers, and neighboring handlers still trigger.

## Risk Checklist

- Reordering handlers can silently change behavior.
- A permissive filter can shadow handlers later in the chain.
- Changes to update mapping should be validated with existing tests and manual bot scenarios.
