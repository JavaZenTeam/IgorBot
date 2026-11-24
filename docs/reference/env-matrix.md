# Environment Variable Matrix

The application binds variables from `src/main/resources/application.yml`.

| Variable | Required | Profile | Purpose |
|---|---|---|---|
| `IGOR_BOT_ACTIVE_PROFILE` | No | all | Active Spring profile (`dev` by default). |
| `IGOR_BOT_USERNAME` | Yes | all | Telegram bot username. |
| `IGOR_BOT_TOKEN` | Yes | all | Telegram bot token. |
| `BASE_URL` | No | all | Base URL used in bot links. |
| `IGOR_BOT_SUPPORT_CHAT` | No | all | Support chat identifier. |
| `PROXY_URL` | No | all | Proxy URL for Telegram API calls. Supports `http://`, `https://`, `socks://`, `socks5://`. Credentials may be embedded: `http://user:pass@host:port`. |
| `POLLY_ACCESS_KEY` | Yes (if `say-text`) | say-text | AWS Polly access key. |
| `POLLY_SECRET_KEY` | Yes (if `say-text`) | say-text | AWS Polly secret key. |
| `FILE_SERVICE_URL` | Yes (if `say-text`) | say-text | External file service URL. |
| `IGOR_DB_CONNECTION_URL` | Yes (if `production`) | production | PostgreSQL JDBC URL. |
| `IGOR_DB_USER` | Yes (if `production`) | production | PostgreSQL user. |
| `IGOR_DB_PASSWORD` | Yes (if `production`) | production | PostgreSQL password. |

## Notes

- Keep secrets in environment or secret storage, never in git.
- If using production profile locally, all `IGOR_DB_*` values are required.
