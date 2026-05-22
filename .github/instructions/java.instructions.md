---
applyTo: "src/main/java/**/*.java,src/test/java/**/*.java,src/main/resources/application.yml,pom.xml"
---

# Java and Spring change instructions

- Preserve package organization under `ru.javazen.telegram.bot`.
- Keep business logic in services; avoid moving logic into controllers.
- Align entity/repository changes with migration strategy.
- For scheduler or handler changes, verify behavior impacts before finalizing.
- Keep compatibility with Java 21 and Spring Boot 3.5.x.

# Required checks

```bash
mvn -B -ntp test
```
