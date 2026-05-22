---
applyTo: ".github/workflows/**/*.yml,scripts/**/*"
---

# CI and script instructions

- Keep CI gates reproducible locally.
- Prefer explicit commands and fail-fast behavior in scripts.
- Update runbook docs when workflow commands change.
- Avoid introducing steps that rely on undocumented local state.

# Expected baseline gates

- Unit tests: `mvn -B -ntp test`
- Build package: `mvn -B -ntp clean package`
