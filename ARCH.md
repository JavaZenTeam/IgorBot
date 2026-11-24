# IgorBot Architecture

## Overview

IgorBot — Spring Boot Telegram bot with message handlers, scheduler/reminders, web admin dashboard, and token-based access control.

- **Runtime:** Java 21, Spring Boot 3.5.13
- **Database:** PostgreSQL (production), H2 (test)
- **Bot API:** `telegrambots` 6.8.0 (Long Polling)
- **AI:** Spring AI (OpenAI-compatible, GPT-4o)
- **Build:** Maven, multi-stage Docker

## Project Structure

```
src/main/java/ru/javazen/telegram/bot/
├── IgorBotApplication.java          # Entry point, @ImportResource("spring/app.xml")
├── CompositeBot.java                # LongPollingBot, chain-of-responsibility router
├── handler/
│   ├── base/                        # UpdateHandler, MessageHandler, CallbackQueryHandler, ...
│   ├── stats/                       # Link-to-admin/chat stats handlers
│   ├── subscriptions/               # Trigger/response subscription handlers
│   ├── toggle/                      # Chat-config toggle handlers
│   └── (25 feature handler classes directly in this package)
├── filter/                          # Filter, MessageFilter, RegexpFilter, RandomFilter, ...
├── scheduler/
│   ├── parser/                      # SpecificTimeParser, ShiftTimeParser
│   └── service/                     # MessageSchedulerServiceImpl
├── model/                           # JPA entities + enums
├── repository/                      # JPA repositories
├── web/                             # MVC controllers (admin, chat, file, token)
├── security/                        # Token auth, WebSecurityConfiguration
├── datasource/                      # Analytics queries + chart data models
├── service/                         # Business services
└── util/                            # Utility classes
src/main/resources/
├── application.yml                  # Runtime config, profiles
├── spring/
│   ├── app.xml                      # Java config, cache, scheduler bean
│   ├── botConfig.xml                # Handler/filter bean wiring (553 lines)
│   ├── answers.xml                  # Weighted answer pools
│   ├── processors.xml               # Text preprocessors
│   ├── songs.xml                    # Song lyrics (~1700 songs)
│   └── toasts.xml                   # Weighted toast phrases
├── templates/                       # Thymeleaf views (16 files)
└── db/migration/                    # Flyway migrations (V1 — V5.4; common/ + postgresql/)
```

## Core Architecture

### Telegram Update Flow

```
Telegram API ──► CompositeBot (Long Polling)
                      │
                      ▼
              Ordered Handler Chain (botConfig.xml)
                      │
              ┌───────┼───────┐
              ▼       ▼       ▼
          Filter?  Filter?  Filter?
              │       │       │
              ▼       ▼       ▼
           Match?  Match?  Match?
              │       │       │
              ▼       ▼       ▼
         Handler  Handler  Handler
              │       │       │
              ▼       ▼       ▼
         Telegram  Telegram  Telegram
         Reply      Reply     Reply
```

**Chain-of-responsibility:** Handlers are registered in a specific order in `botConfig.xml`. `CompositeBot` iterates the chain; the first handler whose filters all pass and returns `true` wins. Order matters — more specific handlers must be registered earlier.

### Spring Configuration

Hybrid Java + XML wiring:

| Config | Role |
|---|---|
| `AppConfig.java` | Java beans: `TaskScheduler`, `CacheManager`, `RestTemplate`, `songEncoder`, profile-specific TTS beans |
| `spring/app.xml` | Imports `botConfig.xml`, wires `AppConfig`, `MessageCollectorService` |
| `spring/botConfig.xml` | All handler/filter bean definitions, wiring of filter lists to `FilterAdapter` |
| `spring/answers.xml` | Weighted pools: yes/no, milestones, start messages, etc. |
| `spring/processors.xml` | Text normalization (pronoun replacement, relative dates) |
| `spring/songs.xml` | Song lyrics by artist |
| `spring/toasts.xml` | Weighted toast phrase pools |

### Handler Framework

**Extensible interfaces** (all delegate to more specific types):

| Interface | Input |
|---|---|
| `UpdateHandler` | `Update` → `boolean` |
| `MessageHandler` | `Message` → `boolean` |
| `TextMessageHandler` | `Message`, `String text` → `boolean` |
| `CallbackQueryHandler` | `CallbackQuery` → `boolean` |
| `InlineQueryHandler` | `InlineQuery` → `boolean` |

**Filters** (all delegate to more specific types):

| Filter | Purpose |
|---|---|
| `Filter` / `MessageFilter` | Base interfaces |
| `RegexpFilter` | Regex match on message text |
| `RandomFilter` | Probabilistic activation |
| `CommandFilter` | Match `/command` |
| `ChatConfigFilter` | Match per-chat config key/value |
| `AllowedChatFilter` / `DeniedChatFilter` | Chat ID whitelist/blacklist |
| `AllowedUserFilter` | User ID whitelist |
| `CallbackQueryFilter` | Callback data prefix match |

**`FilterAdapter`** — Combines multiple filters (AND logic) and multiple handlers; first matching handler wins.

## Scheduler System

### Components

| Layer | Classes | Role |
|---|---|---|
| Parsers | `SpecificTimeParser`, `ShiftTimeParser` | Extract datetime from user text (absolute/relative, with repetition) |
| Entity | `MessageTask` | Persisted task: chatId, messageId, scheduled time, text, repeats, threadId |
| Service | `MessageSchedulerServiceImpl` | `TaskScheduler` orchestration, startup recovery from DB |
| Notify Handlers | `SchedulerNotifyHandler`, `UnschedulerNotifyHandler`, `SchedulerExtendNotifyHandler` | Handle user commands for scheduling/canceling/extending |
| Repo | `MessageTaskRepository` | `getTaskByChatIdAndMessageId` |

### Lifecycle

1. User sends text with date/time → parser creates `MessageTask`
2. `MessageSchedulerServiceImpl` schedules via Spring `TaskScheduler` → `ScheduledFuture`
3. On deadline, sends message to Telegram (with retry/fallback for group migrations)
4. If repeating, re-schedules next occurrence; else deletes from DB

### Startup Recovery

On application start, `MessageSchedulerServiceImpl` loads all pending tasks from DB and reschedules them. Cancellation and extension synchronize both DB and in-memory state.

## Persistence Layer

### Entities

| Entity | Table | Key | Description |
|---|---|---|---|
| `MessageEntity` | `message_entity` | `MessagePK` (chatId+messageId) | Stored Telegram messages with type, content, metadata |
| `MessageTask` | `message_task` | `id` (auto) | Scheduled reminders with time, text, repeat info |
| `ChatConfig` | `chat_config` | `ChatConfigPK` (chatId+key) | Per-chat key-value settings |
| `UserEntity` | `users` | `id` (userId) | Telegram user profile |
| `ChatEntity` | `chats` | `id` (chatId) | Telegram chat metadata |
| `BotUsageLog` | `bot_usage_log` | `MessagePK` | Bot response usage tracking |
| `Subscription` | `subscription` | `MessagePK` | Trigger→response subscriptions |

### Repositories

All extend custom `Repository<T, K>` (wraps `CrudRepository`) or `CrudRepository` directly. Key features:
- `MessageRepository` / `BotUsageLogRepository`: async save (`@Async`)
- `ChatConfigRepository`: in-memory cache via Spring Cache

## Web Admin Dashboard

### Controllers

| Controller | Endpoints | Access |
|---|---|---|
| `AdminController` | `GET /admin`, `/activity-per-entity-chart`, `/chat-types`, `/user-languages` | `/admin` authority |
| `ChatController` | `GET /chat/{chatId}`, `/activity-trend-chart`, `/activity-bar-chart`, `/message-types` | `/chat/{chatId}` authority |
| `FileController` | `GET /file/{fileId}` | Public (proxy download from Telegram) |
| `TokenController` | `GET /stats/{token}` → redirect | Token validation |
| `ErrorPagesController` | Error views (`BasicErrorController`) | Adds bot info to error model |

### Tech Stack

- Spring MVC + Thymeleaf templates (16 views)
- Morris.js for charts (loaded via WebJars)
- `ChatStatisticDataSource` / `UserStatisticDataSource` for analytics
- `ChartDataConverter` converts query results to Morris.js format

### Authentication

Token-based path mapping (no passwords/cookies):

```
User opens: https://bot.com/stats/abcdefgh
              ↓
AuthenticationTokenFilter extracts "abcdefgh" from path
              ↓
AuthenticationTokenService resolves token → path (e.g., /chat/12345)
              ↓
GrantedAuthority = " /chat/12345"
              ↓
@PreAuthorize("hasAuthority('/chat/12345')") allows access
```

- Tokens are 8-char alphanumeric strings
- Stored in-memory (`ConcurrentHashMap`), ephemeral per restart
- `GET /admin/stats/generate/{token}` — admin endpoint to create shareable tokens

## Security

```
WebSecurityConfiguration
├── @EnableMethodSecurity                    # @PreAuthorize support
├── TokenAuthenticationProvider              # Validates tokens → authorities
└── AuthenticationTokenFilter                # Extracts token from URL path
```

Security constraints:
- `/admin/**` requires `/admin` authority
- `/chat/**` requires `/chat/{chatId}` authority for that specific chat
- `/file/**` public (Telegram file proxy)
- `/stats/{token}` token validation flow
- Other endpoints: authenticated or unrestricted per Spring Boot defaults

## Analytics Layer

### Data Flow

```
Users/Messages ──► JPA Repositories
                      │
                      ▼
              DataSource implementations
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
    TopEntities  ActivityTrend  BarChart
    Query        ChartQuery     Query
          │           │           │
          ▼           ▼           ▼
    ChartData (Morris.js format via ChartDataConverter)
```

### Key Components

| Component | Purpose |
|---|---|
| `StatisticDataSource<T>` | Interface for top activity, trends, bar charts |
| `ChatStatisticDataSource` | Chat-level analytics |
| `UserStatisticDataSource` | User-level analytics |
| Query classes (`datasource/query/`) | Typed JPQL/native queries |
| Model classes (`datasource/model/`) | `ChartData`, `MessageStatistic`, `TimeInterval`, etc. |

## Application Profiles

| Profile | Logging | DB | Features |
|---|---|---|---|
| `dev` (default) | DEBUG (web, bot, hibernate) | Default (likely embedded) | All features |
| `production` | INFO | PostgreSQL from env vars | Full production config |
| `say-text` | Same as active | Same as active | AWS Polly TTS, file service |

### Environment Variables

| Variable | Purpose |
|---|---|
| `IGOR_BOT_USERNAME`, `IGOR_BOT_TOKEN` | Telegram bot credentials |
| `IGOR_BOT_ACTIVE_PROFILE` | Spring profile |
| `IGOR_DB_CONNECTION_URL`, `IGOR_DB_USER`, `IGOR_DB_PASSWORD` | Database connection |
| `AI_API_URL`, `AI_API_KEY`, `AI_API_MODEL` | OpenAI-compatible AI |
| `AI_API_TIMEOUT` | AI request timeout in ms (default `30000`) |
| `POLLY_ACCESS_KEY`, `POLLY_SECRET_KEY` | AWS Polly credentials |
| `PROXY_URL` | HTTP proxy for bot |
| `BASE_URL` | Public base URL for web |
| `FILE_SERVICE_URL` | External file service |

## Build & Deployment

### Maven

```bash
mvn -B -ntp test        # Run tests
mvn -B -ntp clean package # Build JAR
mvn -B -ntp spring-boot:run  # Run locally
```

### Docker

Multi-stage build (`Dockerfile`):
1. **Build stage:** Maven 3.9 + Temurin 21 → `mvn package`
2. **Runtime stage:** Eclipse Temurin 21 JRE → copy JAR, expose 8080

### CI/CD (GitHub Actions)

Two-stage pipeline:
1. **validate:** `mvn test` + `mvn package` (JDK 21 Temurin)
2. **docker:** Docker Buildx → push to Docker Hub on tag

## Handler Registration Order

Order in `botConfig.xml` is critical (first match wins). The chain contains additional small handlers; the list below covers the main feature modules in registration order:

1. `milestoneCongratulationsHandler` — Message count celebrations
2. `sayTextHandler` — TTS inline query (profile-dependent)
3. `messageCollectorSaveTextSwitch` — FSB mode toggle (`/fsb_mode_on/off`)
4. `pinnedForwarderSwitch` — Pinned message forwarding toggle
5. `pinnedForwarder` — Pinned message forwarder
6. `continueRepeatableSequenceModule` — Repeatable sequences
7. `statisticReports` — `/admin_stats`
8. `chatStatLinkProvider` — `/chat_stats`
9. `subscription*` — Trigger/response subscriptions (4 beans)
10. `setTimezone` — Timezone setting (`таймзона`)
11. `scheduler` / `schedulerExtend` / `unscheduler` — Reminder scheduling
12. `updateInfoProvider` — `/info` command
13. `replyLayoutSwitcherModule*` — Layout switching (RUS/ENG)
14. `repeaterAdvanced` — "Игорь, скажи <text>"
15. `forwardMessageBridge` — `/forward_to <chat>`
16. `denyWithReverseAnswer` — Negative response sticker
17. `choiceMaker*` — "или" (A or B) choices
18. `doNotKnowAnswerModule` / `randomYesNoAnswerModule` — Random answers
19. `counter` — Counting
20. `getStickerByIdModule` — `/sticker <id>`
21. `lowLevelJokesToggle` / `lowLevelJokes` — Profane jokes toggle
22. `songSingerModule` — Song lyric playback (inline query)
23. `secretAndSacred` / `boysForAll` — Restricted chat handlers
24. `toastHandlerModule` — Toast congratulations
25. `helpCommandHandler` / `helpInline*` — Help system
26. `aiCommandHandlerFilterAdapter` — `/ai` command (Spring AI)

## Database Migrations

22 Flyway migrations (`V1` → `V5.4`): 21 under `db/migration/common/` plus 1 PostgreSQL-specific under `db/migration/postgresql/`, covering:
- Base schema (`message_task`, `subscription`, `chat_config`, `message_entity`)
- `users` / `chats` tables
- `bot_usage_log` table
- Event types and members enums
- Scheduler repetition support
- Dictionary words table
- FSB mode (file hiding)
- User language tracking
- Chat type tracking
- User ID to Long migration
- Message thread ID support

## Test Coverage

17 test files covering:
- Scheduler parsers (SpecificTimeParser, ShiftTimeParser) and notify handlers
- Message handlers (repeater, choice maker, toast, update info provider)
- Utilities (preprocessors, StringMatchRule, SequenceMatcher, MilestoneHelper, SizedItemsContainer)
- Phonetic song matching (DoubleMetaphone) and word splitting
- Framework: JUnit + Mockito (JMockit excluded — conflicts with Mockito on JDK 21+)

## Risk Areas

| Area | Risk | Mitigation |
|---|---|---|
| `application.yml` | Secrets, runtime config | Never commit secrets; env vars |
| `spring/botConfig.xml` | Handler order semantics | Document changes; preserve order |
| `scheduler/` | Time zone, duplicate rescheduling | Review parser + service flows |
| `security/` | Token auth (in-memory, no persistence) | Tokens lost on restart |
| `datasource/` | Query performance on large datasets | Monitor slow queries |
