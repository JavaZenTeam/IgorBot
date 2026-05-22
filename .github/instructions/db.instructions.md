---
applyTo: "src/main/resources/db/migration/**/*.sql,src/main/java/**/model/**/*.java,src/main/java/**/repository/**/*.java"
---

# Database instructions

- Never rewrite existing Flyway migrations.
- Add new migration files for schema changes.
- Keep JPA entity mappings consistent with schema updates.
- Avoid production-only assumptions unless profile-specific behavior is required.

# Required checks

```bash
mvn -B -ntp test
mvn -B -ntp clean package
```
