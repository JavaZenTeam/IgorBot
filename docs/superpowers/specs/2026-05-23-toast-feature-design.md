# Toast Feature Design

## Overview

Add a "toast" (drinking toast) feature to the bot. When someone says something about drinking in a chat, the bot responds with a random Russian drinking toast.

## Trigger Pattern

A `RegexpFilter` matches messages containing drinking-related keywords:

```
(выпь(ем|[ие]ть|ют)|тост\w*|наливай|налито|горько|поднимем\s+бокалы|за\s+встречу|бокалы)
```

## Handler: `ToastHandler`

- Package: `ru.javazen.telegram.bot.handler`
- Implements: `TextMessageHandler`
- Injected: `SizedItemsContainer<String>` with weighted toasts
- Behavior: On each call, picks a random toast (truly random, not seeded by message hash) and sends it as a `SendMessage` reply.
- Returns `true` if a toast was sent, `false` otherwise.

## Toast List

Configured as a `<util:map>` with weighted entries:

| Toast | Weight |
|-------|--------|
| За встречу! | 5 |
| За нас! | 4 |
| Будем здоровы! | 4 |
| За любовь! | 4 |
| Ну, поехали! | 3 |
| Горько! | 3 |
| Выпьем за удачу! | 3 |
| Пусть всё будет хорошо! | 3 |
| До дна! | 2 |
| С Богом! | 2 |
| Будем! | 2 |
| За тех, кто в море! | 2 |
| За присутствующих! | 2 |
| За отсутствующих! | 2 |

## Registration

A `FilterAdapter` bean in `botConfig.xml` wrapping `RegexpFilter` + `ToastHandler`. The bean is added to the `updateHandlers` list.

## Files to Change

1. **New file**: `src/main/java/ru/javazen/telegram/bot/handler/ToastHandler.java`
2. **New/modified**: toast answers config (either new `toasts.xml` import or inline in `botConfig.xml`)
3. **Modified**: `src/main/resources/spring/botConfig.xml` — add `FilterAdapter` bean and register in `updateHandlers`
