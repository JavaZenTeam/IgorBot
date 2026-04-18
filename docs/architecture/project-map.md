# IgorBot Project Map

## High-Level Modules

- `src/main/java/ru/javazen/telegram/bot/` - main Java package
- `src/main/resources/spring/` - XML-defined bot pipeline and bean wiring
- `src/main/resources/db/migration/` - Flyway migration scripts
- `src/main/resources/templates/` - Thymeleaf views
- `src/test/java/` - tests

## Runtime Composition

1. Spring Boot starts from `IgorBotApplication`.
2. XML resources from `spring/app.xml` and `spring/botConfig.xml` define handler/filter wiring.
3. `CompositeBot` receives Telegram updates and routes through ordered `UpdateHandler` chain.
4. Persistence and statistics flow through JPA repositories and datasource query components.
5. Web controllers expose chat/admin stats and token-based access flows.

## Package Responsibilities

- `handler/` - feature handlers that process updates.
- `filter/` - reusable predicates for handler activation.
- `scheduler/` - parsing, scheduling, and notification logic for delayed messages.
- `service/` - business services and integration orchestration.
- `repository/` + `model/` - persistence.
- `web/` - MVC endpoints and view rendering.
- `security/` - token auth and method-level access checks.
- `datasource/` - analytics-oriented queries and chart data models.

## Key Entry Files

- `src/main/java/ru/javazen/telegram/bot/IgorBotApplication.java`
- `src/main/java/ru/javazen/telegram/bot/CompositeBot.java`
- `src/main/resources/spring/botConfig.xml`
- `src/main/resources/application.yml`
- `pom.xml`

## Agent Navigation Tips

- For behavior changes to update handling, inspect `botConfig.xml` ordering before code edits.
- For scheduler bugs, inspect parsers under `scheduler/parser/` and service under `scheduler/service/`.
- For stats/web issues, inspect `web/` plus matching `datasource/query/` classes.
