# Verification Runbook

## Goal

Ensure a change is safe for review and CI before opening a PR.

## Baseline Verification

Windows:

```powershell
./scripts/verify.ps1
```

Unix-like:

```bash
./scripts/verify.sh
```

## Manual Equivalent

```powershell
mvn -B -ntp test
mvn -B -ntp clean package
```

## For Behavior Changes

- Handler changes: confirm no unintended chain-order side effects.
- Scheduler changes: validate create/extend/cancel flows.
- Security/web changes: verify endpoint access behavior and token flow.

## PR Checklist

- Tests pass locally.
- Build artifact can be produced.
- Docs updated when behavior or commands changed.
- No secrets or environment-specific credentials committed.
