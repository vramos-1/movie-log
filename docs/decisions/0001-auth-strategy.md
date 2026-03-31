# ADR 0001 - Auth Strategy

Status: Proposed

Use JWT-based stateless authentication for API requests.

## Rationale
- Scales well for SPA + API architecture.
- Keeps backend session management minimal.

## Next Steps
- Finalize JWT claims.
- Configure signing key management per environment.
